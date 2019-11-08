package com.demo.common;

import org.jboss.arquillian.container.spi.client.deployment.DeploymentScenario;
import org.jboss.arquillian.container.spi.event.DeployManagedDeployments;
import org.jboss.arquillian.container.spi.event.UnDeployManagedDeployments;
import org.jboss.arquillian.container.spi.event.container.BeforeStop;
import org.jboss.arquillian.container.test.impl.client.deployment.event.GenerateDeployment;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.EventContext;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.arquillian.test.spi.annotation.ClassScoped;
import org.jboss.arquillian.test.spi.annotation.SuiteScoped;
import org.jboss.arquillian.test.spi.event.suite.AfterSuite;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReuseDeploymentExtension implements LoadableExtension {

    private static final Logger LOG = Logger.getLogger(ReuseDeploymentExtension.class.getName());

    @Override
    public void register(ExtensionBuilder builder) {
        builder.observer(ReuseDeploymentHandler.class);
    }

    private static class ReuseDeploymentHandler {

        private Class<?> activeReusableDeploymentClass;
        private final Map<Class<?>, DeploymentScenario> generatedReusableDeploymentScenarios = new HashMap<>();
        private boolean blockDeploy;
        private boolean blockUnDeploy;

        @Inject
        private Event<GenerateDeployment> generateDeploymentEvent;

        @Inject
        private Event<UnDeployManagedDeployments> unDeployManagedDeploymentsEvent;

        @Inject
        @ClassScoped
        private InstanceProducer<DeploymentScenario> deploymentScenarioProducer;

        @Inject
        @SuiteScoped
        private InstanceProducer<DeploymentScenario> suiteScopedDeploymentScenarioProducer;

        public void generateDeployment(@Observes EventContext<GenerateDeployment> eventCtx) {
            // just proceed if event has been fired from this method (see further down)
            if (eventCtx.getEvent() instanceof GenerateReusableDeployment) {
                eventCtx.proceed();
                return;
            }
            blockDeploy = false;
            blockUnDeploy = false;

            // evaluate @ReuseDeployment on test class (if present)
            final TestClass testClass = eventCtx.getEvent().getTestClass();
            final Class<?> deploymentClassToReuse = Optional.ofNullable(testClass.getAnnotation(ReuseDeployment.class))
                    .map(ReuseDeployment::value)
                    .orElse(null);

            // skip deploy/undeploy if the active reusable deployment class is the same as the one required by the test class
            if (deploymentClassToReuse != null && deploymentClassToReuse == activeReusableDeploymentClass) {
                LOG.log(Level.FINE, "Reusing deployed Deployments for {0}: {1}", new Object[] { testClass.getName(), activeReusableDeploymentClass });
                deploymentScenarioProducer.set(generatedReusableDeploymentScenarios.get(deploymentClassToReuse));
                blockDeploy = true;
                blockUnDeploy = true;
                return;
            }

            // undeploy deployed reusable deployment because test class requires another reusable or a non-reusable deployment
            if (activeReusableDeploymentClass != null) {
                LOG.log(Level.FINE, "Undeploying for {0}: {1}", new Object[] { testClass.getName(), activeReusableDeploymentClass });
                deploymentScenarioProducer.set(generatedReusableDeploymentScenarios.get(activeReusableDeploymentClass));
                unDeployManagedDeploymentsEvent.fire(new UnDeployManagedDeployments());
            }

            // just proceed without any futher special handling in case the test class requires a non-reusable deployment
            if (deploymentClassToReuse == null) {
                LOG.log(Level.FINE, "Performing default deployment procedure for {0}", testClass.getName());
                eventCtx.proceed();
                return;
            }

            // test class requires reusable deployment which is no deployed and may not even have been generated
            final DeploymentScenario deploymentScenario = generatedReusableDeploymentScenarios.get(deploymentClassToReuse);
            if (deploymentScenario != null) {
                LOG.log(Level.FINE, "Reusing generated Deployments for {0}: {1}", new Object[] { testClass.getName(), deploymentClassToReuse });
                deploymentScenarioProducer.set(deploymentScenario);
            } else {
                LOG.log(Level.FINE, "Generating reusable deployment for {0}: {1}", new Object[] { testClass.getName(), deploymentClassToReuse });
                generateDeploymentEvent.fire(new GenerateReusableDeployment(new TestClass(deploymentClassToReuse)));
                generatedReusableDeploymentScenarios.put(deploymentClassToReuse, deploymentScenarioProducer.get());
            }
            activeReusableDeploymentClass = deploymentClassToReuse;
            blockUnDeploy = true;
        }

        public void deploy(@Observes EventContext<DeployManagedDeployments> eventCtx) {
            if (blockDeploy) {
                LOG.log(Level.FINE, "Blocking deployment, activeReusableDeploymentClass: {0}",
                        Optional.ofNullable(activeReusableDeploymentClass).map(Class::getName).orElse("-"));
            } else {
                eventCtx.proceed();
            }
        }

        public void undeploy(@Observes EventContext<UnDeployManagedDeployments> eventCtx) {
            if (blockUnDeploy) {
                LOG.log(Level.FINE, "Blocking undeployment, activeReusableDeploymentClass: {0}",
                        Optional.ofNullable(activeReusableDeploymentClass).map(Class::getName).orElse("-"));
            } else {
                eventCtx.proceed();
                activeReusableDeploymentClass = null;
            }
        }

        public void undeploy(@Observes BeforeStop event) {
            if (activeReusableDeploymentClass != null) {
                LOG.log(Level.FINE, "Undeploying reusable deployment before stopping container: {0}", activeReusableDeploymentClass.getName());
                blockUnDeploy = false;
                suiteScopedDeploymentScenarioProducer.set(generatedReusableDeploymentScenarios.get(activeReusableDeploymentClass));
                unDeployManagedDeploymentsEvent.fire(new UnDeployManagedDeployments());
            }
        }

        public void cleanUp(@Observes(precedence = Integer.MIN_VALUE) AfterSuite event) {
            generatedReusableDeploymentScenarios.clear();
        }

        private static class GenerateReusableDeployment extends GenerateDeployment {

            public GenerateReusableDeployment(TestClass testClass) {
                super(testClass);
            }
        }
    }
}
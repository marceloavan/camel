/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.ignite.compute;

import java.util.Map;

import org.apache.camel.Category;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.component.ignite.AbstractIgniteEndpoint;
import org.apache.camel.component.ignite.ClusterGroupExpression;
import org.apache.camel.component.ignite.IgniteConstants;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;

import static org.apache.camel.component.ignite.IgniteConstants.SCHEME_COMPUTE;

/**
 * Run <a href="https://apacheignite.readme.io/docs/compute-grid">compute operations</a> on an Ignite cluster.
 * 
 * You can pass an IgniteCallable, an IgniteRunnable, an IgniteClosure, or collections of them, along with their
 * parameters if necessary. This endpoint only supports producers.
 */
@UriEndpoint(firstVersion = "2.17.0", scheme = SCHEME_COMPUTE, title = "Ignite Compute", syntax = "ignite-compute:endpointId",
             category = { Category.COMPUTE }, producerOnly = true, headersClass = IgniteConstants.class)
public class IgniteComputeEndpoint extends AbstractIgniteEndpoint {

    @UriPath
    @Metadata(required = true)
    private String endpointId;

    @UriParam(label = "producer")
    private ClusterGroupExpression clusterGroupExpression;

    @UriParam(label = "producer")
    @Metadata(required = true)
    private IgniteComputeExecutionType executionType;

    @UriParam(label = "producer")
    private String taskName;

    @UriParam(label = "producer")
    private String computeName;

    @UriParam(label = "producer")
    private Long timeoutMillis;

    public IgniteComputeEndpoint(String uri, String remaining, Map<String, Object> parameters,
                                 IgniteComputeComponent igniteComponent) {
        super(uri, igniteComponent);
    }

    @Override
    public Producer createProducer() throws Exception {
        return new IgniteComputeProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("The Ignite Compute endpoint does not support consumers.");
    }

    public IgniteCompute createIgniteCompute() {
        Ignite ignite = ignite();
        IgniteCompute compute = clusterGroupExpression == null
                ? ignite.compute() : ignite.compute(clusterGroupExpression.getClusterGroup(ignite));

        if (computeName != null) {
            compute = compute.withName(computeName);
        }

        if (timeoutMillis != null) {
            compute = compute.withTimeout(timeoutMillis);
        }

        return compute;
    }

    /**
     * Gets the endpoint ID.
     */
    public String getEndpointId() {
        return endpointId;
    }

    /**
     * The endpoint ID (not used).
     */
    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
    }

    /**
     * Gets the cluster group expression.
     */
    public ClusterGroupExpression getClusterGroupExpression() {
        return clusterGroupExpression;
    }

    /**
     * An expression that returns the Cluster Group for the IgniteCompute instance.
     */
    public void setClusterGroupExpression(ClusterGroupExpression clusterGroupExpression) {
        this.clusterGroupExpression = clusterGroupExpression;
    }

    /**
     * Gets the execution type of this producer.
     */
    public IgniteComputeExecutionType getExecutionType() {
        return executionType;
    }

    /**
     * The compute operation to perform. Possible values: CALL, BROADCAST, APPLY, EXECUTE, RUN, AFFINITY_CALL,
     * AFFINITY_RUN. The component expects different payload types depending on the operation.
     */
    public void setExecutionType(IgniteComputeExecutionType executionType) {
        this.executionType = executionType;
    }

    /**
     * Gets the task name, only applicable if using the {@link IgniteComputeExecutionType#EXECUTE} execution type.
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * The task name, only applicable if using the {@link IgniteComputeExecutionType#EXECUTE} execution type.
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * Gets the name of the compute job, which will be set via {@link IgniteCompute#withName(String)}.
     */
    public String getComputeName() {
        return computeName;
    }

    /**
     * The name of the compute job, which will be set via {@link IgniteCompute#withName(String)}.
     */
    public void setComputeName(String computeName) {
        this.computeName = computeName;
    }

    /**
     * Gets the timeout interval for triggered jobs, in milliseconds, which will be set via
     * {@link IgniteCompute#withTimeout(long)}.
     */
    public Long getTimeoutMillis() {
        return timeoutMillis;
    }

    /**
     * The timeout interval for triggered jobs, in milliseconds, which will be set via
     * {@link IgniteCompute#withTimeout(long)}.
     */
    public void setTimeoutMillis(Long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

}

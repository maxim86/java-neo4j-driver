/*
 * Copyright 2018-2019 The OpenTracing Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.opentracing.contrib.neo4j;

import io.opentracing.Span;
import io.opentracing.Tracer;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;

public class TracingTransactionWork<T> implements TransactionWork<T> {

  private final Tracer tracer;

  private final Span parent;

  private final TransactionWork<T> transactionWork;

  public TracingTransactionWork(TransactionWork<T> transactionWork, Span parent, Tracer tracer) {
    this.transactionWork = transactionWork;
    this.tracer = tracer;
    this.parent = parent;
  }

  @Override
  public T execute(Transaction tx) {
    try {
      parent.log("execute");
      return transactionWork.execute(new TracingTransaction(tx, parent, tracer));
    } catch (Exception e) {
      TracingHelper.onError(e, parent);
      throw e;
    }
  }
}

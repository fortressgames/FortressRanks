package net.fortressgames.database;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QueryHandler<T> {

	private OnEventFunction<T> onComplete;
	private OnEventFunction<Exception> onError;
	private OnFinallyFunction onFinally;

	private final queryContext<T> context;

	private static final ExecutorService executer = Executors.newSingleThreadExecutor();

	@FunctionalInterface
	public interface OnEventFunction<T> {
		void execute(T object);
	}

	@FunctionalInterface
	public interface OnFinallyFunction {
		void execute();
	}

	@FunctionalInterface
	public interface queryContext<T> {
		T execute();
	}

	public QueryHandler(queryContext<T> context) {
		this.context = context;
	}

	/**
	 * Execute when the query finished on the main server thread.
	 */
	public QueryHandler<T> onComplete(OnEventFunction<T> onComplete) {
		this.onComplete = onComplete;
		return this;
	}

	public QueryHandler<T> onError(OnEventFunction<Exception> onError) {
		this.onError = onError;
		return this;
	}

	public QueryHandler<T> onFinally(OnFinallyFunction onFinally) {
		this.onFinally = onFinally;
		return this;
	}

	/**
	 * Execute the query on a new thread.
	 */
	public void execute() {
		executer.execute(() -> {
			try {
				T result = context.execute();
				if (this.onComplete != null) {
					onComplete.execute(result);
				}
			}
			catch (Exception e) {
				if (this.onError != null)
					this.onError.execute(e);
				else
					throw e;
			}
			finally {
				if (this.onFinally != null)
					this.onFinally.execute();
			}
		});
	}

	/**
	 * Execute the query on the same tread.
	 * Only recommended when you need to wait for the result of this query.
	 */
	public void executeSync() {
		try {
			T result = context.execute();
			if (this.onComplete != null) {
				onComplete.execute(result);
			}
		}
		catch (Exception e) {
			if (this.onError != null)
				this.onError.execute(e);
			else
				throw e;
		}
		finally {
			if (this.onFinally != null)
				this.onFinally.execute();
		}
	}
}
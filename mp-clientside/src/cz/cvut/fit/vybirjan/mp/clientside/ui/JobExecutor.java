package cz.cvut.fit.vybirjan.mp.clientside.ui;

import java.util.concurrent.Executor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * Helper class which executes runnables in eclipse Jobs
 * 
 * @author Jan Vybíral
 * 
 */
public class JobExecutor implements Executor {

	public JobExecutor(String jobName) {
		this.jobName = jobName;
	}

	private final String jobName;

	@Override
	public void execute(final Runnable command) {
		Job j = new Job(jobName) {

			@Override
			protected IStatus run(IProgressMonitor arg0) {
				command.run();
				return Status.OK_STATUS;
			}
		};
		j.schedule();
	}

}

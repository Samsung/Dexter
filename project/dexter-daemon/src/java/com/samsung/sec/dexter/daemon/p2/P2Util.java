package com.samsung.sec.dexter.daemon.p2;

import java.net.URI;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;

import com.samsung.sec.dexter.daemon.DexterDaemonActivator;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;


/**
 * Source code from
 * <ul>
 * <li><a href="http://wiki.eclipse.org/Equinox/p2/Adding_Self-Update_to_an_RCP_Application">Equinox Wiki pages, Adding self update</a></li>
 * </ul>
 * 
 * @author mahieddine.ichir@free.fr
 */
public class P2Util {

	/**
	 * Check for application updates.
	 * @param agent
	 * @param monitor
	 * @return
	 * @throws OperationCanceledException
	 */
	public static IStatus checkForUpdates(IProvisioningAgent agent, IProgressMonitor monitor) throws OperationCanceledException {
		ProvisioningSession session = new ProvisioningSession(agent);
		UpdateOperation operation = new UpdateOperation(session);
		
		SubMonitor sub = SubMonitor.convert(monitor, "Checking for application updates...", 200);
		IStatus status =  operation.resolveModal(sub.newChild(100));
		DexterDaemonActivator.LOG.info("Check Result; " + status.toString());
		return status;
	}
	
	public static IStatus checkForUpdates2(IProvisioningAgent agent) throws OperationCanceledException {
		ProvisioningSession session = new ProvisioningSession(agent);
		UpdateOperation operation = new UpdateOperation(session);
		IStatus status =  operation.resolveModal(new NullProgressMonitor());
		DexterDaemonActivator.LOG.info("Check Result; " + status.toString());
		return status;
	}
	
	/**
	 * Download and install application updates.
	 * @param agent
	 * @param monitor
	 * @return
	 * @throws OperationCanceledException
	 */
	public static IStatus installUpdates(IProvisioningAgent agent, IProgressMonitor monitor) throws OperationCanceledException {
		DexterDaemonActivator.LOG.error("run installUpdates....");
		
		
		ProvisioningSession session = new ProvisioningSession(agent);
		UpdateOperation operation = new UpdateOperation(session);
		SubMonitor sub = SubMonitor.convert(monitor, "Installing updates ...", 200);
		operation.resolveModal(sub.newChild(100));
		ProvisioningJob job = operation.getProvisioningJob(monitor);
		
		IStatus status =  job.runModal(sub.newChild(100));
		
		DexterDaemonActivator.LOG.info(">>>>>>>>>>>>>>>>>>>>>>UPdate Result; " + status.toString());
		
		return status;
	}
	
	public static IStatus installUpdates2(IProvisioningAgent agent) throws OperationCanceledException {
		DexterDaemonActivator.LOG.error("run installUpdates....");
		
		ProvisioningSession session = new ProvisioningSession(agent);
		UpdateOperation operation = new UpdateOperation(session);
		operation.resolveModal(new NullProgressMonitor());
		
		ProvisioningJob job = operation.getProvisioningJob(new NullProgressMonitor());
		IStatus status =  job.runModal(new NullProgressMonitor());
		
		DexterDaemonActivator.LOG.info(">>>>>>>>>>>>>>>>>>>>>>UPdate Result; " + status.toString());
		
		return status;
	}

	/**
	 * Add a repository to declared updates repositories.
	 * @param repo
	 * @return
	 */
	public static boolean addRepository(IProvisioningAgent agent, String repo) {
		IMetadataRepositoryManager metadataManager = (IMetadataRepositoryManager) agent.getService(IMetadataRepositoryManager.SERVICE_NAME);
		IArtifactRepositoryManager artifactManager = (IArtifactRepositoryManager) agent.getService(IArtifactRepositoryManager.SERVICE_NAME);
		if (metadataManager == null) {
			DexterDaemonActivator.LOG.error("metadataManager is null!!!");
			return false;
		}
		if (artifactManager == null) {
			DexterDaemonActivator.LOG.error("artifactManager is null!!!");
			return false;
		}
		
		try {
			URI uri = new URI(repo);
			metadataManager.addRepository(uri);
			artifactManager.addRepository(uri);
			
			DexterDaemonActivator.LOG.error("repository is added : " + uri.toString());
			return true;
		} catch (Exception e) {
			EclipseUtil.errorMessageBox("failed when add repository", e.getMessage() + " - " + repo);
			DexterDaemonActivator.LOG.error(e.getMessage(), e);
			return false;
		}
	}
}
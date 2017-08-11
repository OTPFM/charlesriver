package hu.farago.charlesriver.web.dnd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;

import com.vaadin.server.StreamVariable;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.Notification;

import hu.farago.charlesriver.web.Callback;

public class UploadStreamVariable implements StreamVariable {

	private static final long serialVersionUID = -5099940624073163346L;
	private Html5File file;
	private FileOutputStream fos;
	private File destination;
	private Callback<File> uploadFinishedCallback;

	public UploadStreamVariable(Callback<File> uploadFinishedCallback) {
		this.uploadFinishedCallback = uploadFinishedCallback;
	}

	public void setFile(Html5File file) {
		this.file = file;
	}

	public FileOutputStream getFos() {
		return fos;
	}

	@Override
	public OutputStream getOutputStream() {
		fos = null;
		try {
			destination = new File(FileUtils.getTempDirectoryPath() + file.getFileName());
			fos = new FileOutputStream(destination);
		} catch (FileNotFoundException e) {
			Notification.show("Unable to open temp directory path");
		}
		return fos;
	}

	@Override
	public boolean listenProgress() {
		return true;
	}

	@Override
	public void onProgress(StreamingProgressEvent event) {
		Notification.show("Progress, bytesReceived=" + event.getBytesReceived());
	}

	@Override
	public void streamingStarted(StreamingStartEvent event) {
		Notification.show("Stream started, fileName=" + event.getFileName());
	}

	@Override
	public void streamingFinished(StreamingEndEvent event) {
		Notification.show("Stream finished, fileName=" + event.getFileName());
		uploadFinishedCallback.callback(destination);
	}

	@Override
	public void streamingFailed(StreamingErrorEvent event) {
		Notification.show("Stream failed, fileName=" + event.getFileName());
	}

	@Override
	public boolean isInterrupted() {
		return false;
	}

}

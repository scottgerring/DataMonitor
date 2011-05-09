package org.instk.datamonitor;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;

public class YachtieClient {
	
	private Socket sendingSocket;
	private OutputStreamWriter socketStream;
	private boolean isOpen;
	
	private long lastHeadingTime = 0;
	private long lastGPSTime = 0;
	
	private long updateThreshold = 2000;
	
	public YachtieClient(String server, int port) {
		isOpen = false;
		try {
			sendingSocket = new Socket(server,8081);
			socketStream = new OutputStreamWriter(sendingSocket.getOutputStream());
			isOpen = true;
		} catch (IOException e) {
			System.out.println("Failed opening socket: " + e);
		}
	}
	
	public void recordGPS(double lat, double lng, double alt, double accuracy) {
		long now = new Date().getTime();
		if (now - lastGPSTime > updateThreshold) {
			streamLog(String.format("GPS,%f,%f,%f,%f\n", 
					lat,
					lng,
					alt,
					accuracy));
			lastGPSTime = now;
		}
	}
	
	public void recordHeading(float heading) {
		long now = new Date().getTime();
		if (now - lastHeadingTime > updateThreshold) {
			streamLog(String.format("3,%f", heading));
			lastHeadingTime = now;
		}
	}
	
	public boolean isOpen() {
		return isOpen;
	}
	
	private void streamLog(String message)
	{
		if (sendingSocket != null && sendingSocket.isConnected()) {
			try {
				socketStream.write(message);
				socketStream.flush();
			} catch (IOException e) {
				System.out.println("Error writing! Whoops");
			}
		}
	}
}

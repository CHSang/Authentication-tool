package vn.axonactive.authentication;

import java.util.TimerTask;

public class FrozenDestroyerTask extends TimerTask {
	String userName;

	public FrozenDestroyerTask(String userName) {
		super();
		this.userName = userName;
	}

	@Override
	public void run() {
		IpStorage.stopFrozenDestroyer(userName);
	}
}

package miniJava;


public abstract class Statement{

	private boolean isLeader;
	
	public void setIsLeader(boolean isLeader) {
		this.isLeader = isLeader;
	}
	
	public boolean isLeader() {
		return isLeader;
	}
	
	public abstract boolean isControlTransferStatement();
	
}
package edu.cmu.eventtracker.tests;

public class Bootstrap {
	public static void main(String[] args) throws Exception {
		BasicUnitTest t = new BasicUnitTest();
		t.before();
		t.initShards();
		t.startGeoServers(4);
	}

}

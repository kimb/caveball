package test;

import network.*;
import core.*;

import java.io.*;
import java.util.*;

public class ClientTest {
    public static void main(String[] args) {
	
	String host;
	
	if (args.length < 1) {
	    host = "localhost";
	    System.out.println("No host name given. Host: " + host);
	}
	else  {
	    host = args[0];
	    System.out.println("Host: " + host);
	}
	
	ClientTester tester = new ClientTester(new NetworkImpl(), host);
    }
}

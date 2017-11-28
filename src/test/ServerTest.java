package test;

import network.*;
import core.*;

import java.io.*;
import java.util.*;

public class ServerTest {
    public static void main(String[] args) {
	
	ServerTester tester = new ServerTester(new NetworkImpl());
    }
}

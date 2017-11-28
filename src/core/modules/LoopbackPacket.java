package core.modules;

import core.*;
import core.packets.*;
import util.*;
import java.io.*;

/**
 * T�t� networkPakettia k�ytet��n kun sy�tet��n osa paketista uudelleen
 * itsellemme.
 */
public class LoopbackPacket implements NetworkPacket {
	protected Serializable data;
	public LoopbackPacket(Serializable data) {
		this.data = data;
	}
	public Serializable getData() { return data; }
	public int getPing() { return 0; }
	public Object getSource() { return null; }
}

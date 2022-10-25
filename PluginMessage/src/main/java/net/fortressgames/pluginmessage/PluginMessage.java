package net.fortressgames.pluginmessage;

import lombok.Getter;
import lombok.ToString;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@ToString
public class PluginMessage implements Serializable {

	@Serial	private static final long serialVersionUID = -2190101168498868754L;

	// The action
	@Getter	private final String action;
	// Should the message display a debug when it gets to the requested server
	@Getter private final boolean silent;
	// Ares values of the message
	@Getter private final List<Object> args;

	/**
	 * @param args The objects must be serializable!
	 */
	public PluginMessage(String action, boolean silent, Object... args) {
		this.action = action;
		this.silent = silent;
		this.args = Arrays.asList(args);
	}

	public byte[] toByteArray() {
		try {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
			objStream.writeObject(this);

			return byteStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
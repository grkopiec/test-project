package pl.grkopiec.utils;

public class StringUtils {
	public static final String label = "1234567890qwertyuiopasdfghjkl;'[]-=j`zxc,./lkjhgfd1234567890qwertyuiopasdfghjkl;'[]-=j`zxc,./lkjhgfd";
	public static final byte[] frame = label.getBytes();
	public static final String chunkLabel = "1234567890qwertyuiop";
	public static final byte[] chunkFrame = chunkLabel.getBytes();
	public static final String endLabel = "end";
	public static final byte[] endFrame = endLabel.getBytes();
}

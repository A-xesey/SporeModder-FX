package sporemodder.file.pdr;

import java.io.IOException;
import java.sql.Array;

import sporemodder.file.filestructures.Stream.StringEncoding;
import sporemodder.file.filestructures.StreamReader;


public class AssetBlock {
    private String assetName;
    private String extension;
    private String chunk;

	public String getName() {
		return assetName;
	}
	public String getExtension() {
		return extension;
	}
	public String getChunk() {
		return chunk;
	}

    public void read(StreamReader in, int version, String ext) throws IOException {
        extension = ext;
        if (version == 0x302E3120 || version == 1) {
            assetName = in.readString(StringEncoding.ASCII, 0x19);
            
            int index = assetName.indexOf('\u0020');
            if (index > 0) assetName = assetName.substring(0, index);
        }
        else if (version == 2) {
            assetName = in.readCString(StringEncoding.ASCII);
        }
        else if (version == 3) {
            assetName = in.readString(StringEncoding.ASCII, in.readLEInt());
            extension = in.readString(StringEncoding.ASCII, 4);
        }
        else throw new IOException("AssetBlock Error: unsupported EAPD Binary Patch version: " + version);

        byte[] b = new byte[in.readLEInt()];
        in.readBytes(b);
        chunk = new String(b);
    }
}

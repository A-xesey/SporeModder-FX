package sporemodder.file.otdb;

import java.io.IOException;

import sporemodder.file.prop.PropertyList;
import sporemodder.file.argscript.ArgScriptWriter;
import sporemodder.file.filestructures.MemoryStream;
import sporemodder.file.filestructures.StreamReader;

public class AdventureStatisticsFile {
    private static final int VERSION = 3;

    public long assetID;
	public final PropertyList propertyList = new PropertyList();
    
	public void read(StreamReader stream) throws Exception {
        int version = stream.readLEInt();
        if (version != VERSION)
			throw new IOException("Unsupported version: " + version);

        assetID = readLELong(stream);
        int count = stream.readLEInt();
        MemoryStream temp = new MemoryStream(4 + (0xC * count));    // count + all properties
        temp.writeInt(count);
        for (int i = 0; i < count; i++) {
            temp.writeInt(stream.readLEInt());
            temp.writeShort(stream.readLEShort());
            temp.writeShort(stream.readLEShort());
            temp.writeInt(stream.readLEInt());
        }
        temp.seek(0);
        propertyList.read(temp);
        temp.close();
    }
    
	public ArgScriptWriter toArgScript() {
		ArgScriptWriter writer = new ArgScriptWriter();
        writer.command("assetID").arguments(assetID);
        writer.blankLine();
        
        writer.arguments(propertyList.toArgScript());

        return writer;
    }

	private static long readLELong(StreamReader stream) throws IOException {
		long value1 = stream.readLEUInt();
		long value2 = stream.readLEUInt();
		return (value2 << 32) | (value1 & 0xFFFFFFFFL);
	}
}
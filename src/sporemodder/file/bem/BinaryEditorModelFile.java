package sporemodder.file.bem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sporemodder.file.argscript.ArgScriptWriter;
import sporemodder.file.filestructures.StreamReader;

public class BinaryEditorModelFile {
	private static final int MAGIC = 0x48657ED3;
	private static final int VERSION = 5;
	private static final int MINVERSION = 3;
	private static final int MAXBLOCKS = 0x400;
	private static final int MAXEXPANSIONPACKAGE = 0x80;
	private static final int BLOCKLENGTH = 0x1d8;
	
	public int version = VERSION;
    public int totalBlocks;
    public int expansionPackage;
    
	public final BinaryEditorModelProperties properties = new BinaryEditorModelProperties();
	public final List<BinaryEditorModelBlock> blocks = new ArrayList<>();

    public void read(StreamReader in) throws IOException {
		if (in.readLEInt() != MAGIC)
			throw new IOException("BEM Error: unexpected MAGIC, file pointer " + in.getFilePointer());

        version = in.readLEInt();
        if (version < MINVERSION || version > VERSION)
			throw new IOException("BEM Error: unexpected version, file pointer " + in.getFilePointer());

        totalBlocks = in.readLEInt();
        if (totalBlocks > MAXBLOCKS)
			throw new IOException("BEM Error: unexpected total blocks, file pointer " + in.getFilePointer());

        expansionPackage = in.readLEInt();
        if (expansionPackage > MAXEXPANSIONPACKAGE)
			throw new IOException("BEM Error: unexpected field_8 value, file pointer " + in.getFilePointer());

        int size = (version == VERSION) ? 0x80 : 0x44;
        byte[] buffer = new byte[size];
        RLEChunk.read(in, buffer, size);
        properties.read(buffer, size);

        size = totalBlocks * BLOCKLENGTH;
        buffer = new byte[size];
        RLEChunk.read(in, buffer, size);
        for (int index = 0; index < totalBlocks; index++) {
            BinaryEditorModelBlock block = new BinaryEditorModelBlock();
            block.read(buffer, index * BLOCKLENGTH);
            blocks.add(block);
        }
    }
	
	public void toArgScript(ArgScriptWriter writer) {
		properties.toArgScript(writer);
		writer.blankLine();

		writer.command("blocks").ints(totalBlocks);
		for (BinaryEditorModelBlock b : blocks)
			b.toArgScript(writer);
	}
	
	public ArgScriptWriter toArgScript() {
		ArgScriptWriter writer = new ArgScriptWriter();
		toArgScript(writer);
		return writer;
	}
}

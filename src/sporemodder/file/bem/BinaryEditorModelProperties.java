package sporemodder.file.bem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import sporemodder.HashManager;
import sporemodder.file.argscript.ArgScriptWriter;
import sporemodder.file.filestructures.StreamReader;
import sporemodder.util.ColorRGB;

public class BinaryEditorModelProperties {
    public int modelType;
    public int zCorpScore;
    public final int[] skinEffectIDs = new int[3];
    public final int[] skinEffectSeeds = new int[3];
    public final ColorRGB[] skinColors = new ColorRGB[3];
    public final byte[] padding = new byte[60];

    public void read(StreamReader in) throws IOException {
        modelType = in.readLEInt();
        zCorpScore = in.readLEInt();
        in.readLEInts(skinEffectIDs);
        in.readLEInts(skinEffectSeeds);
        skinColors[0].readLE(in);
        skinColors[1].readLE(in);
        skinColors[2].readLE(in);
        in.read(padding);
    }
    public void read(byte[] data, int size) throws IOException {
        ByteBuffer in = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        modelType = in.getInt();
        zCorpScore = in.getInt();

        for (int i = 0; i < 3; i++)
            skinEffectIDs[i] = in.getInt();

        for (int i = 0; i < 3; i++)
            skinEffectSeeds[i] = in.getInt();

        for (int i = 0; i < 3; i++)
            skinColors[i] = new ColorRGB(in.getFloat(), in.getFloat(), in.getFloat());
            
        if (size > 0x44) in.get(padding);
    }
    
	public void toArgScript(ArgScriptWriter writer) {
		writer.command("properties").startBlock();
		writer.command("modeltype").arguments(HashManager.get().getFileName(modelType));

		writer.command("skineffect1").arguments(HashManager.get().getFileName(skinEffectIDs[0]));
		writer.command("skineffect2").arguments(HashManager.get().getFileName(skinEffectIDs[1]));
		writer.command("skineffect3").arguments(HashManager.get().getFileName(skinEffectIDs[2]));

		writer.command("skineffectseed1").ints(skinEffectSeeds[0]);
		writer.command("skineffectseed2").ints(skinEffectSeeds[1]);
		writer.command("skineffectseed3").ints(skinEffectSeeds[2]);

		writer.command("skincolor1").color(skinColors[0]);
		writer.command("skincolor2").color(skinColors[1]);
		writer.command("skincolor3").color(skinColors[2]);

		writer.command("zcorpscore").ints(zCorpScore);

		writer.endBlock().commandEND();
	}
}

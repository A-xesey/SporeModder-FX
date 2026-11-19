package sporemodder.file.pdr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.util.converter.ByteStringConverter;
import sporemodder.file.argscript.ArgScriptWriter;
import sporemodder.file.filestructures.StreamReader;
import sporemodder.file.filestructures.Stream.StringEncoding;

public class PatchDataResourceFile {
    private static final int MAGIC = 0x44504145;     // EAPD
    private static final int VERSION1 = 0x302E3120;  // 1.0

    public static int length;
	public final List<AssetBlock> assets = new ArrayList<>();
	public final List<AssetBlock> samples = new ArrayList<>();

    public void read(StreamReader in) throws IOException {
		if (in.readLEInt() != MAGIC)
			throw new IOException("PDR Error: unexpected MAGIC, file pointer " + in.getFilePointer());

        int version = in.readLEInt();
        if (version != VERSION1 && (version < 1 || version > 3))
            throw new IOException("PDR Error: unsupported EAPD Binary Patch version (" + version + "), file pointer " + in.getFilePointer());
        
        int platform = in.readLEInt();
        if (platform != 2)
			throw new IOException("PDR Error: unsupported platform type (" + platform + "), file pointer " + in.getFilePointer());

        length = in.readLEInt();
        int totalAssets = in.readLEInt();
        int totalSamples = in.readLEInt();
        
        for (int i = 0; i < totalAssets; i++) {
            AssetBlock asset = new AssetBlock();
            asset.read(in, version, "pd");
            assets.add(asset);
        }
        for (int i = 0; i < totalSamples; i++) {
            AssetBlock sample = new AssetBlock();
            sample.read(in, version, "snr");
            samples.add(sample);
        }
    }
    public void toArgScript(File outputFolder) throws FileNotFoundException {
		if (!outputFolder.exists()) outputFolder.mkdir();
        
		for (int i = 0; i < assets.size(); i++) {
			AssetBlock asset = assets.get(i);
			if (asset.getName() == null || asset.getName().isEmpty()) return;
			File file = new File(outputFolder, String.format("%s.%s", asset.getName(), asset.getExtension()));
			
			ArgScriptWriter writer = new ArgScriptWriter();
			writer.arguments(asset.getChunk());
			writer.write(file);
		}
		for (int i = 0; i < samples.size(); i++) {
			AssetBlock sample = samples.get(i);
			if (sample.getName() == null || sample.getName().isEmpty()) return;
			File file = new File(outputFolder, String.format("%s.%s", sample.getName(), sample.getExtension()));
			
			ArgScriptWriter writer = new ArgScriptWriter();
			writer.arguments(sample.getChunk());
			writer.write(file);
		}
    }
}

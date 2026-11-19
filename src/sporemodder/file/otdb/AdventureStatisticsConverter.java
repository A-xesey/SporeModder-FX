package sporemodder.file.otdb;

import java.io.File;
import java.io.PrintWriter;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import sporemodder.HashManager;
import sporemodder.ProjectManager;
import sporemodder.UIManager;
import sporemodder.file.Converter;
import sporemodder.file.ResourceKey;
import sporemodder.file.dbpf.DBPFPacker;
import sporemodder.file.filestructures.FileStream;
import sporemodder.file.filestructures.StreamReader;
import sporemodder.file.filestructures.StreamWriter;
import sporemodder.util.ProjectItem;

public class AdventureStatisticsConverter implements Converter {
	private static final int TYPE_ID = 0xFD60F0C1;
	private static String extension = null;
	
	private boolean decode(StreamReader stream, File outputFile) throws Exception {
		AdventureStatisticsFile adv_stats = new AdventureStatisticsFile();
		adv_stats.read(stream);
		
		try (PrintWriter out = new PrintWriter(outputFile)) {
			out.println(adv_stats.toArgScript());
		};
		
		return true;
	}
	
	@Override
	public boolean decode(StreamReader stream, File outputFolder, ResourceKey key) throws Exception {
		return decode(stream, Converter.getOutputFile(key, outputFolder, "adv_stats_t"));
	}

	@Override
	public boolean encode(File input, StreamWriter output) throws Exception {
		return false;
	}

	@Override
	public boolean encode(File input, DBPFPacker packer, int groupID) throws Exception {
		return false;
	}

	@Override
	public boolean isDecoder(ResourceKey key) {
		return key.getTypeID() == TYPE_ID;
	}

	private void checkExtensions() {
		if (extension == null) {
			extension = HashManager.get().getTypeName(TYPE_ID);
		}
	}
	
	@Override
	public boolean isEncoder(File file) {
		return false;
	}

	@Override
	public String getName() {
		return "Adventure Statistics (." + HashManager.get().getTypeName(TYPE_ID) + ")";
	}

	@Override
	public boolean isEnabledByDefault() {
		return false;
	}

	@Override
	public int getOriginalTypeID(String extension) {
		checkExtensions();
		return TYPE_ID;
	}
	
	@Override
	public void generateContextMenu(ContextMenu contextMenu, ProjectItem item) {
		if (!item.isRoot()) {
			
			if (item.isMod() && isEncoder(item.getFile())) {
			}
			else {
				ResourceKey key = ProjectManager.get().getResourceKey(item);
				
				if (isDecoder(key)) {
					MenuItem menuItem = new MenuItem("Unpack adventure statistics");
					menuItem.setMnemonicParsing(false);
					menuItem.setOnAction(event -> {
						final File outputFolder = Converter.getOutputFile(key, item.getFile().getParentFile(), "adv_stats_t");
						boolean result = UIManager.get().tryAction(() -> {
							try (FileStream stream = new FileStream(item.getFile(), "r")) {
								decode(stream, outputFolder);
								
								ProjectManager.get().selectItem(ProjectManager.get().getSiblingItem(item, outputFolder.getName()));
							}
						}, "Cannot decode file.");
						if (!result) {
							// Delete the file, as it hasn't been written properly
							outputFolder.delete();
						}
					});
					contextMenu.getItems().add(menuItem);
				}
			}
		}
	}
}

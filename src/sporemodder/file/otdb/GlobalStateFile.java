package sporemodder.file.otdb;

import java.util.ArrayList;
import java.util.List;

import sporemodder.HashManager;
import sporemodder.file.argscript.ArgScriptEnum;
import sporemodder.file.argscript.ArgScriptWriter;
import sporemodder.file.effects.ResourceID;
import sporemodder.file.filestructures.StreamReader;
import sporemodder.file.rw4.RWKeyframe;

public class GlobalStateFile {
    private static final int VERSION = 1;
    private static final int SERILAIZER_CLASS_ID = 0x01A80D26;

	// public static final ArgScriptEnum ENUM_CROSS_GAMEDATA = new ArgScriptEnum();
	// static {
	// 	ENUM_CROSS_GAMEDATA.add(0xabce0000, "mAchievementSet"); 
	// 	ENUM_CROSS_GAMEDATA.add(0xabce0004, "mEncounteredCreatures");
	// 	ENUM_CROSS_GAMEDATA.add(0xabce0005, "mTribeGamesCompleted");
	// 	ENUM_CROSS_GAMEDATA.add(0xabce0006, "mEatenCreatures");
	// 	ENUM_CROSS_GAMEDATA.add(0xabce0007, "mCellGameIDs");
	// }

    public final List<ResourceID> achievements = new ArrayList<>();
    public final List<Integer> mAchievementSet = new ArrayList<>();
    public final List<Integer> mEncounteredCreatures = new ArrayList<>();
    public final List<Integer> mTribeGamesCompleted = new ArrayList<>();
    public final List<Integer> mEatenCreatures = new ArrayList<>();
    public final List<Integer> mCellGameIDs = new ArrayList<>();

	public void read(StreamReader stream) throws Exception {
        int version = stream.readInt();
        if (version != VERSION)
            throw new Exception("Unsupported version: " + version);

        int count = stream.readInt();
        for (int i = 0; i < count; i++) {
            ResourceID achievement = new ResourceID();
            achievement.setGroupID(stream.readInt());
            achievement.setInstanceID(stream.readInt());
            achievements.add(achievement);
        }
        
        int serializerID = stream.readInt();
        if (serializerID != SERILAIZER_CLASS_ID)
            throw new Exception("Unexpected Serializer Class: " + serializerID);
        
        stream.skip(4);             // SerializerSize
        count = stream.readInt();
        stream.skip(count*8);         // CrossGameData IDs and their lengths

        readList(stream, mAchievementSet);
        readList(stream, mEncounteredCreatures);
        readList(stream, mTribeGamesCompleted);
        readList(stream, mEatenCreatures);
        readList(stream, mCellGameIDs);
    }

    public ArgScriptWriter toArgScript() {
        ArgScriptWriter writer = new ArgScriptWriter();

		writer.command("mAchievements"); writer.startBlock();
		for (ResourceID achieve : achievements) {
            writer.indentNewline();
            writer.arguments(HashManager.get().getFileName(achieve.getGroupID()), achieve.getInstanceID());
        }
        writer.endBlock(); writer.commandEND();

        printList(writer, mAchievementSet, "mAchievementSet");
        printList(writer, mEncounteredCreatures, "mEncounteredCreatures");
        printList(writer, mTribeGamesCompleted, "mTribeGamesCompleted");
        printList(writer, mEatenCreatures, "mEatenCreatures");
        printList(writer, mCellGameIDs, "mCellGameIDs");

        return writer;
    }
    private void readList(StreamReader stream, List<Integer> list) throws Exception {
        int count = stream.readInt();
        for (int i = 0; i < count; i++) list.add(stream.readInt());
    }

    private void printList(ArgScriptWriter writer, List<Integer> list, String name) {
		writer.command(name); writer.startBlock();
		for (Integer el : list) {
			writer.indentNewline();
            writer.arguments(HashManager.get().getFileName(el));
        }
        writer.endBlock(); writer.commandEND();
    }
}

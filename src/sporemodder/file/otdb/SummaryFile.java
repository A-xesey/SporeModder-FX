package sporemodder.file.otdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import sporemodder.HashManager;
import sporemodder.file.argscript.ArgScriptArguments;
import sporemodder.file.argscript.ArgScriptEnum;
import sporemodder.file.argscript.ArgScriptParser;
import sporemodder.file.argscript.ArgScriptStream;
import sporemodder.file.argscript.ArgScriptWriter;
import sporemodder.file.filestructures.StreamReader;
import sporemodder.file.filestructures.StreamWriter;

public class SummaryFile {
	
	public static final int TYPE_INT = 0x02E1A75D;
	public static final int TYPE_FLOAT = 0x02E1A7FF;
	
	public static final ArgScriptEnum ENUM_TYPE = new ArgScriptEnum();
	static {
		ENUM_TYPE.add(TYPE_INT, "int");
		ENUM_TYPE.add(TYPE_FLOAT, "float");
	}
	
	public static final ArgScriptEnum ENUM_PARAMETERS = new ArgScriptEnum();
	static {
		ENUM_PARAMETERS.add(0x2DD90AF, "type");
		ENUM_PARAMETERS.add(0x2DC9D1E, "subtype");
		ENUM_PARAMETERS.add(0x5B06E36, "cost");
		ENUM_PARAMETERS.add(0x2F05C60, "baseGear");
		ENUM_PARAMETERS.add(0x7358629A, "height");
		ENUM_PARAMETERS.add(0x2F05C58, "carnivore");
		ENUM_PARAMETERS.add(0x2F05C59, "herbivore");
		ENUM_PARAMETERS.add(0x2F05C5E, "cuteness");
		ENUM_PARAMETERS.add(0x2F05C5F, "totalSocial");
		ENUM_PARAMETERS.add(0x5B15AA5, "social");
		ENUM_PARAMETERS.add(0x2F05C61, "numGraspers");
		ENUM_PARAMETERS.add(0x3FEA210, "meanLookingScore");
		ENUM_PARAMETERS.add(0x3FEA1A0, "totalAttack");
		ENUM_PARAMETERS.add(0x5B15A92, "attack");
		ENUM_PARAMETERS.add(0x3FEA1C0, "numFeet");
		ENUM_PARAMETERS.add(0x4AB3BD8, "biteCapRange");
		ENUM_PARAMETERS.add(0x4AB3BD9, "strikeCapRange");
		ENUM_PARAMETERS.add(0x4AB3BDA, "chargeCapRange");
		ENUM_PARAMETERS.add(0x4AB3BDB, "spitCapRange");
		ENUM_PARAMETERS.add(0xF42136D5, "singCapRange");
		ENUM_PARAMETERS.add(0xF42136D6, "danceCapRange");
		ENUM_PARAMETERS.add(0xF42136D7, "charmCapRange");
		ENUM_PARAMETERS.add(0xF42136D8, "poseCapRange");
		ENUM_PARAMETERS.add(0x5AC2B96, "glideCapRange");
		ENUM_PARAMETERS.add(0x5AC2B97, "stealthCapRange");
		ENUM_PARAMETERS.add(0x5AC2B98, "sprintCapRange");
		ENUM_PARAMETERS.add(0x68DE3E8, "senseCapRange");
		ENUM_PARAMETERS.add(0x5AC2B99, "healthCapRange");
		
		ENUM_PARAMETERS.add(0x2E0FCC74, "editorModelSaveLastChild");
		ENUM_PARAMETERS.add(0x99524FAF, "editorModelForceSaveOver");
		ENUM_PARAMETERS.add(0xAE773A29, "editorModelCopyConsequence");
		ENUM_PARAMETERS.add(0xEC2942CD, "isMyCaptain");

		
		/// General
		ENUM_PARAMETERS.add(0x05fb4759, "published");				// kParameterIDPublished
		ENUM_PARAMETERS.add(0x03cc89b1, "contentType");					// kParameterIDThemeID
		ENUM_PARAMETERS.add(0x05dc0b88, "contentSource");			// kParameterIDContentSource
		ENUM_PARAMETERS.add(0x061ef9b1, "timeDownloaded");			// kParameterTimeDownloaded
		ENUM_PARAMETERS.add(0xcd6e902c, "hasgaprop");				// kParameterIDGAProp
		ENUM_PARAMETERS.add(0x54a32960, "validation_playable");		// kParameterIDValidationPlayable
		ENUM_PARAMETERS.add(0x54a32961, "validation_pollinatable");	// kParameterIDValidationPollinatable
		ENUM_PARAMETERS.add(0x0665f917, "validation_viewableLarge");	// kParameterIDValidationViewableLarge
		ENUM_PARAMETERS.add(0x067b82d8, "validation_editable");		// kParameterIDValidationEditable
		ENUM_PARAMETERS.add(0x17a1c72d, "expansionPackage");			// kParameterIDExpansionPackage

		/// Creature
		ENUM_PARAMETERS.add(0x052def3f, "speciesArchetype");			// kParameterIDCreatureSpeciesArchetype
		ENUM_PARAMETERS.add(0x052def4b, "speciesArchetypeQuery");	// kParameterIDCreatureSpeciesArchetypeQuery

		/// Vehicle
		ENUM_PARAMETERS.add(0x0429d47d, "defense");					// kParameterIDVehicleDefense
		ENUM_PARAMETERS.add(0x0429d47e, "speed");					// kParameterIDVehicleSpeed
		ENUM_PARAMETERS.add(0x0429d47c, "power");					// kParameterIDVehiclePower
		ENUM_PARAMETERS.add(0x05b950d7, "totalScore");				// kParameterIDVehicleTotalScore
		ENUM_PARAMETERS.add(0x05c96ff0, "vehicleType");				// kParameterIDVehicleType

		/// Vehicle/Building
		// F08 Build
		ENUM_PARAMETERS.add(0x05de94e6, "BoundingBoxX");				// kParameterIDBoundingBoxX
		ENUM_PARAMETERS.add(0x05de94e7, "BoundingBoxY");				// kParameterIDBoundingBoxY
		ENUM_PARAMETERS.add(0x05de94e8, "BoundingBoxZ");				// kParameterIDBoundingBoxZ
		ENUM_PARAMETERS.add(0x05de94e9, "BoundingBoxTotal");			// kParameterIDBoundingBoxTotal (F08)

		// Release
		ENUM_PARAMETERS.add(0x05de94ea, "BoundingBoxHeight"); 		// kParameterIDBoundingBoxMaxZ
		ENUM_PARAMETERS.add(0x05e62409, "boundingBoxTotal");			// kParameterIDBoundingBoxTotal

		// Adventure
		ENUM_PARAMETERS.add(0x15e8afc8, "completed");				// kParameterIDAdventureCompleted (in Space Stage?)
		ENUM_PARAMETERS.add(0xb91fba14, "cannotEdit");				// kParameterIDAdventureCannotEdit
		ENUM_PARAMETERS.add(0xd22f5e35, "hasMissingAsset");			// kParameterIDAdventureMissingAsset
		ENUM_PARAMETERS.add(0x5a3584a7, "avatarLocked");				// kParameterIDAdventureAvatarLocked
		ENUM_PARAMETERS.add(0xdb4675dd, "invalidVersion");			// kParameterIDAdventureInvalidVersion
	}
	
	public static final ArgScriptEnum ENUM_AMTAGS = new ArgScriptEnum();
	static {
		ENUM_AMTAGS.add(0x1F71E85, "BlockID");
		ENUM_AMTAGS.add(0x4EA801F, "EditorGroup");
		ENUM_AMTAGS.add(0x4EAB460, "GroupCount");
		ENUM_AMTAGS.add(0x4EBFEBD, "BlockTag");
		ENUM_AMTAGS.add(0x5833344, "TextureTag");
		ENUM_AMTAGS.add(0x518AF1D, "Creator");
		ENUM_AMTAGS.add(0x68375D7, "ACPSet");
		ENUM_AMTAGS.add(0x518B883, "Feed");
		ENUM_AMTAGS.add(0x52B27B5, "RegionColor");
		ENUM_AMTAGS.add(0x52C812A, "BaseColor");
		ENUM_AMTAGS.add(0x52C8CA5, "Texture");
	}
	
	public static class AMTag {
		public int tagGroup;
		public int tagID;
		public float value;
	}

	public static final ArgScriptEnum ENUM_CONTENTSOURCE = new ArgScriptEnum();
	static {
		ENUM_CONTENTSOURCE.add(0, "assembled");
		ENUM_CONTENTSOURCE.add(1, "matched");
		ENUM_CONTENTSOURCE.add(2, "imported");
	}
	
	public final Map<Integer, Number> parameters = new LinkedHashMap<>();
	public final List<AMTag> amTags = new ArrayList<>();
	
	public void read(StreamReader stream) throws IOException {
		int version = stream.readInt();
		if (version != 4)
			throw new IOException("Unsupported .summary version " + version);
		
		int count = stream.readInt();
		for (int i = 0; i < count; i++) {
			int id = stream.readInt();
			int type = stream.readInt();
			
			if (type == TYPE_INT)
			{
				parameters.put(id, stream.readInt());
			}
			else if (type == TYPE_FLOAT)
			{
				parameters.put(id, stream.readFloat());
			}
			else {
				throw new IOException("Unknown param type " + HashManager.get().getFileName(type));
			}
		}
		
		count = stream.readInt();
		for (int i = 0; i < count; i++) {
			AMTag tag = new AMTag();
			tag.tagGroup = stream.readInt();
			tag.tagID = stream.readInt();
			tag.value = stream.readFloat();
			amTags.add(tag);
		}
	}
	
	public void write(StreamWriter stream) throws IOException {
		stream.writeInt(4);
		stream.writeInt(parameters.size());
		for (Map.Entry<Integer, Number> entry : parameters.entrySet()) {
			stream.writeInt(entry.getKey());
			if (entry.getValue() instanceof Integer) {
				stream.writeInt(TYPE_INT);
				stream.writeInt(entry.getValue().intValue());
			}
			else {
				stream.writeInt(TYPE_FLOAT);
				stream.writeFloat(entry.getValue().floatValue());
			}
		}
		stream.writeInt(amTags.size());
		for (AMTag tag : amTags) {
			stream.writeInt(tag.tagGroup);
			stream.writeInt(tag.tagID);
			stream.writeFloat(tag.value);
		}
	}
	
	public ArgScriptWriter toArgScript() {
		ArgScriptWriter writer = new ArgScriptWriter();
		toArgScript(writer);
		return writer;
	}
	
	public void toArgScript(ArgScriptWriter writer) {
		for (Map.Entry<Integer, Number> entry : parameters.entrySet()) {
			String valueString;
			if (entry.getValue() instanceof Integer) {
				valueString = HashManager.get().formatInt32(entry.getValue().intValue());
			}
			else {
				valueString = entry.getValue().toString();
			}
			
			String parameterString = ENUM_PARAMETERS.get(entry.getKey());
			if (parameterString == null) {
				parameterString = HashManager.get().hexToString(entry.getKey());
			}

			if (parameterString == "contentSource") {
				String enumValue = ENUM_CONTENTSOURCE.get(entry.getValue().intValue());
				if (enumValue != null) valueString = enumValue;
			}
			
			writer.command("parameter").arguments(
					ENUM_TYPE.get(entry.getValue() instanceof Integer ? TYPE_INT : TYPE_FLOAT),
					parameterString, 
					valueString);
		}
		writer.blankLine();
		for (AMTag tag : amTags) {
			String tagString = ENUM_AMTAGS.get(tag.tagGroup);
			if (tagString == null) {
				tagString = HashManager.get().getFileName(tag.tagGroup);
			}
			writer.command("amTag").arguments(
					tagString,
					HashManager.get().getFileName(tag.tagID),
					tag.value
					);
		}
	}
	
	public void clear() {
		parameters.clear();
		amTags.clear();
	}
	
	public ArgScriptStream<SummaryFile> generateStream() {
		ArgScriptStream<SummaryFile> stream = new ArgScriptStream<>();
		stream.setData(this);
		stream.addDefaultParsers();
		
		stream.addParser("parameter", ArgScriptParser.create((parser, line) -> {
			final ArgScriptArguments args = new ArgScriptArguments();
			if (line.getArguments(args, 3)) {
				
				int id;
				if (args.get(1).charAt(0) == '0') {
					id = Optional.ofNullable(stream.parseFileID(args, 1)).orElse(0);
				}
				else {
					id = ENUM_PARAMETERS.get(args, 1);
				}
				
				Number value;
				int type = ENUM_TYPE.get(args, 0);
				if (type == TYPE_INT) {
					if (id != 0x05dc0b88) value = stream.parseInt(args, 2);
					else {
						value = ENUM_CONTENTSOURCE.get(args.get(2));
					}
				}
				else {
					value = stream.parseFloat(args, 2);
				}
				
				parameters.put(id, value);
			}
		}));
		
		stream.addParser("amTag", ArgScriptParser.create((parser, line) -> {
			final ArgScriptArguments args = new ArgScriptArguments();
			if (line.getArguments(args, 3)) {
				AMTag tag = new AMTag();

				if (args.get(0).charAt(0) == '0') {
					tag.tagGroup = Optional.ofNullable(stream.parseFileID(args, 0)).orElse(0);
				}
				else {
					tag.tagGroup = ENUM_AMTAGS.get(args, 0);
				}
				
				tag.tagID = Optional.ofNullable(stream.parseFileID(args, 1)).orElse(0);
				tag.value = Optional.ofNullable(stream.parseFloat(args, 2)).orElse(0f);
				
				amTags.add(tag);
			}
		}));
		
		return stream;
	}
}

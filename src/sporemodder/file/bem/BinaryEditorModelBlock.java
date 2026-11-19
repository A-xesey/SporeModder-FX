package sporemodder.file.bem;

import sporemodder.util.Vector3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import sporemodder.HashManager;
import sporemodder.file.argscript.ArgScriptWriter;
import sporemodder.util.ColorRGB;
import sporemodder.util.Matrix;

public class BinaryEditorModelBlock {
    public int groupID;
    public int instanceID;
    public int parentIndex;
    public int symmetricIndex;
    public float scale;
    public Vector3 position = new Vector3();
    public Vector3 triangleDirection = new Vector3();
    public Vector3 trianglePickOrigin = new Vector3();
    public Matrix orientation = Matrix.getIdentity();
    public Matrix userOrientation = Matrix.getIdentity();
    public boolean snapped;
    public boolean isAsymmetric;
    public int limbType;
    public float muscleScale;
    public float baseMuscleScale;
    public int handlesCount;
    public float[] handleWeights = new float[8];
    public int[] handleChannels = new int[8];
    public int paintListCount;
    public int[] paintRegions = new int[8];
    public int[] paintIDs = new int[8];
    public ColorRGB[] paintColors1 = new ColorRGB[8];
    public ColorRGB[] paintColors2 = new ColorRGB[8];

    public void read(byte[] data, int index) throws IOException {
        ByteBuffer in = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        in.position(index);
        groupID = in.getInt();
        instanceID = in.getInt();
        parentIndex = in.getInt();
        symmetricIndex = in.getInt();
        scale = in.getFloat();

        position.setX(in.getFloat());
        position.setY(in.getFloat());
        position.setZ(in.getFloat());

        triangleDirection.setX(in.getFloat());
        triangleDirection.setY(in.getFloat());
        triangleDirection.setZ(in.getFloat());

        trianglePickOrigin.setX(in.getFloat());
        trianglePickOrigin.setY(in.getFloat());
        trianglePickOrigin.setZ(in.getFloat());
        
		for (int i = 0; i < 3; i++) {
			for (int f = 0; f < 3; f++) {
				orientation.m[i][f] = in.getFloat();
			}
		}

		for (int i = 0; i < 3; i++) {
			for (int f = 0; f < 3; f++) {
				userOrientation.m[i][f] = in.getFloat();
			}
		}
        snapped = Boolean.getBoolean(Byte.toString(in.get()));
        isAsymmetric = Boolean.getBoolean(Byte.toString(in.get()));
        in.getShort();
        limbType = in.getInt();
        muscleScale = in.getFloat();
        baseMuscleScale = in.getFloat();
        handlesCount = in.getInt();

        for (int i = 0; i < 8; i++)
            handleWeights[i] = in.getFloat();

        for (int i = 0; i < 8; i++)
            handleChannels[i] = in.getInt();
        
        paintListCount = in.getInt();

        for (int i = 0; i < 8; i++)
            paintRegions[i] = in.getInt();

        for (int i = 0; i < 8; i++)
            paintIDs[i] = in.getInt();

        for (int i = 0; i < 8; i++)
            paintColors1[i] = new ColorRGB(in.getFloat(), in.getFloat(), in.getFloat());
            
        for (int i = 0; i < 8; i++)
            paintColors2[i] = new ColorRGB(in.getFloat(), in.getFloat(), in.getFloat());
    }
    
	public void toArgScript(ArgScriptWriter writer) {
		writer.command("blockref").startBlock();
        if (parentIndex != 255)
            writer.command("parentindex").ints(parentIndex);
        if (symmetricIndex != 255)
            writer.command("symmetricindex").ints(symmetricIndex);

		writer.command("blockID").arguments(HashManager.get().getFileName(groupID), HashManager.get().getFileName(instanceID));

        writer.command("transform").startBlock();
            if (limbType != 3) {
                writer.command("limbtype").ints(limbType);
                writer.command("musclescale").floats(muscleScale);
                writer.command("basemusclescale").floats(baseMuscleScale);
            }
            writer.command("scale").floats(scale);
            writer.command("position").vector3(position);
            writer.command("triangledirection").vector3(triangleDirection);
            writer.command("trianglepickorigin").vector3(trianglePickOrigin);

            writer.command("orientation").startBlock();
                writer.command("row0").floats(orientation.m[0][0], orientation.m[0][1], orientation.m[0][2]);
                writer.command("row1").floats(orientation.m[1][0], orientation.m[1][1], orientation.m[1][2]);
                writer.command("row2").floats(orientation.m[2][0], orientation.m[2][1], orientation.m[2][2]);
            writer.endBlock().commandEND();
        
        boolean writeUserOrienation = false;
        Matrix basematrix = Matrix.getIdentity();
        for (int i = 0; i < 3; i++) {
            for (int x = 0; x < 3; x++) {
                writeUserOrienation = userOrientation.m[i][x] != basematrix.m[i][x]; 
                if (writeUserOrienation) break;
            }
            if (writeUserOrienation) break;
        }
        if (writeUserOrienation) {
            writer.command("userorientation").startBlock();
                writer.command("row0").floats(userOrientation.m[0][0], userOrientation.m[0][1], userOrientation.m[0][2]);
                writer.command("row1").floats(userOrientation.m[1][0], userOrientation.m[1][1], userOrientation.m[1][2]);
                writer.command("row2").floats(userOrientation.m[2][0], userOrientation.m[2][1], userOrientation.m[2][2]);
            writer.endBlock().commandEND();
        }
        writer.endBlock().commandEND();
        
        writer.flag("snapped", snapped);

        if (handlesCount > 0) {
            writer.command("handles").ints(handlesCount).startBlock();
            for (int i = 0; i < handlesCount; i++) {
                writer.command("channel").arguments(HashManager.get().getFileName(handleChannels[i])).floats(handleWeights[i]);
            }
            writer.endBlock().commandEND();
        }

        if (paintListCount > 0) {
            writer.command("paintlist").ints(paintListCount).startBlock();
            for (int i = 0; i < paintListCount; i++) {
                writer.command("paint").startBlock();
                    writer.command("paintregion").ints(paintRegions[i]);
                    writer.command("paintid").arguments(HashManager.get().getFileName(paintIDs[i]));
                    writer.command("color1").color(paintColors1[i]);
                    writer.command("color2").color(paintColors2[i]);
                writer.endBlock().commandEND();
            }
            writer.endBlock().commandEND();
        }

        writer.flag("isasymmetric", isAsymmetric);

		writer.endBlock().commandEND();
	}
}

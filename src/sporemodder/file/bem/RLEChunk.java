package sporemodder.file.bem;

import java.io.IOException;
import java.util.Arrays;

import sporemodder.file.filestructures.StreamReader;

public class RLEChunk {
	private static final int MAXLENGTH = 0x800;
	private static final int RLEFLAG = -2;

    public static void read(StreamReader in, byte[] data, int size) throws IOException {
        int index = 0, length = 0;
        byte previousByte = 0;
        while (index < size) {
            if (length <= 0) {
                length = in.readLEUShort();

                if (length == 0 || length > MAXLENGTH)
                    throw new IOException("RLEChunk Error: unexpected length, file pointer " + in.getFilePointer());
            }
            byte currentbyte = in.readByte(); length--;
            if (currentbyte == RLEFLAG) {
                byte nextByte = in.readByte(); length--;
                if (nextByte != 0) {
                    int count = nextByte & 0xff;
                    Arrays.fill(data, index, index + count, previousByte);
                    index += count;
                 } else {
                    previousByte = data[index];
                    data[index++] = nextByte;
                 }
            } else {
                previousByte = data[index];
                data[index++] = currentbyte ;
            }
        }
    }
}

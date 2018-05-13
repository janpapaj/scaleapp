package papajj.scaleapp;

/**
 * Created by jan.papaj on 8/6/2016.
 */
public class crc16 {
    public static int Calculate(byte[] data, int length, int offset) {
        int crc;
        int buf;
        int i;

        if(offset >= length)
            return 0;

        crc = 0;
        for( i = offset; i < offset + length; i++)
        {
            buf = data[i];
            crc ^= ( buf << 8) & 0xffff;
            for(int j = 0; j < 8; j++)
            {
                if( (crc & 0x8000) != 0)
                {
                    crc = (( crc << 1) ^ 0x1021) & 0xffff;
                }
                else
                {
                    crc = (crc << 1) & 0xffff;
                }
            }
        }

        return crc;
    }
}

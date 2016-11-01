//package com.example.tomcat.mpchrarct;
package com.example.tomcat.mpchrarct;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.String.format;


/**
 * Created by tomcat on 2016/6/3.
 */
public class Utils
{
    private static final String TAG = Utils.class.getSimpleName();
    static final String HEXES = "0123456789ABCDEF";

    public Utils()
    {}

    public static byte[] ackDateTime(byte fnCMDByte)
    {
        Calendar mCal = Calendar.getInstance();
        //final int   cmdLength=12;
        byte[]  cmdByte = {0x4D, (byte)0xFD, 0x00, 0x08, (byte)fnCMDByte, 0x01
                ,(byte)(mCal.get(Calendar.YEAR)-2000)
                ,(byte)(mCal.get(Calendar.MONTH)+1)
                ,(byte)(mCal.get(Calendar.DATE))
                ,(byte)(mCal.get(Calendar.HOUR_OF_DAY))
                ,(byte)(mCal.get(Calendar.MINUTE))
                //,(byte)(mCal.get(Calendar.SECOND))
                ,0x00
        };

        cmdByte[cmdByte.length-1] = (byte) countCS(cmdByte);        // check sum

        return cmdByte;
    }

    public static byte[] ackMACAddress(byte cmdByte, String macAddr)
    {
        String strAddr = removeColon(macAddr);
        byte[] byteAddr  = hexStringToByteArray(strAddr);
        byte[] tmpInfo = new byte[]{  0x4D, (byte) 0xFD, 0x00, 0x08, (byte) 0xA1,
                (byte) byteAddr[0], (byte) byteAddr[1], (byte) byteAddr[2],
                (byte) byteAddr[3], (byte) byteAddr[4], (byte) byteAddr[5], 0x00};

        tmpInfo[tmpInfo.length-1] = (byte)(countCS(tmpInfo) & 0x00ff);   //CS

        return tmpInfo;
    }

    public static String shortFileName(String subName)
    {
        int[] tmpByte = currentDateTime();
        String tmpStr = Integer.toString(tmpByte[0]) +
                        Integer.toString(tmpByte[1]) +
                        Integer.toString(tmpByte[2]) ;
        Log.d(TAG, "shortFileName(): " + tmpStr);
        return (tmpStr + subName);
    }


    public static int[] currentDateTime()
    {
        Calendar    mCal = Calendar.getInstance();
        int[]       tmp = new int[6];

        tmp[0] =  mCal.get(Calendar.YEAR);
        tmp[1] =  mCal.get(Calendar.MONTH)+1;
        tmp[2] =  mCal.get(Calendar.DATE);
        tmp[3] =  mCal.get(Calendar.HOUR_OF_DAY);
        tmp[4] =  mCal.get(Calendar.MINUTE);
        tmp[5] =  mCal.get(Calendar.SECOND);
        return tmp;
    }

    public static String makeFileName()
    {

        int[] tmp = currentDateTime();
        /*
        String tmpStr1 = "";
        for (int aTmp : tmp)
        {
            tmpStr1 += Integer.toString(aTmp);
        }
        return tmpStr1;
        */

        return (new String(format("%04d%02d%02d%02d%02d%02d",
                tmp[0], tmp[1], tmp[2], tmp[3], tmp[4], tmp[5])));
    }

    public static String getCurrentDateTime()
    {
        int[] tmp = currentDateTime();

        return (new String(format("%04d/%02d/%02d  %02d:%02d:%02d",
                tmp[0], tmp[1], tmp[2], tmp[3], tmp[4], tmp[5])));
    }

    public static void writeLogFile(List<byte[]> DataList)
    {
        //Environment.getExternalStorageDirectory().getPath()
        String  fileName = "/sdcard/" + makeFileName() + ".log";
        byte[]  nextLine = {0x0D, 0x0A};

        Log.d(TAG, "log file: " + fileName);
        try
        {
            FileOutputStream    fOut = new FileOutputStream(new File(fileName), true);
            for (int i=0; i<DataList.size(); i++)
            {
                fOut.write(getHexToString(DataList.get(i)).getBytes());
                fOut.write(nextLine);
            }

            fOut.close();
            Log.d(TAG, "write log file Ok.");
        }
        catch (FileNotFoundException e)
        {
            //e.printStackTrace();
            Log.d(TAG, "File or Path Not found !");
        }
        catch (IOException e)
        {
            //e.printStackTrace();
            Log.d(TAG, "write File fail !");
        }
    }

    public static ArrayList<byte[]> readLogFile(String fileName)
    {
        int lineCunts=0;
        ArrayList<byte[]>   byteData = new ArrayList<>();
        File sdcard = Environment.getExternalStorageDirectory();

        //Get the text file
        File file = new File(sdcard, fileName);
        //File file = new File(sdcard, "20161024.log");
        //File file = new File("20161024.log");
        //System.out.println("readFile()" + file);
        Log.d(TAG, "log file: " + file);

        //Read text from file
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null)
            {
                byteData.add(hexStringToByteArray(line));
                //System.out.println("readFile(),[" + (lineCunts++) + "] raw data line: " + line);
                Log.d(TAG, "readLogFile() ,[" + (lineCunts++) + "] raw data line: " + line);
            }
            br.close();
        }
        catch (IOException e)
        {
            //You'll need to add proper error handling here
            //System.out.println("readFile()" + e.toString());
            Log.e(TAG, "readLogFile(), " + e.toString());
            e.printStackTrace();
        }

        // debug message
        /*
        byte[] dateTime = new byte[5];
        for (int i=0; i<5; i++)
            dateTime[i] = byteData.get(0)[i];
        //System.out.println("readFile(), dateTime: " + getHexToString(dateTime));
        Log.e(TAG, "readLogFile(), date/Time: " + getHexToString(dateTime));
        */
        return byteData;
    }

    public static ArrayList<byte[]> getDateTime(ArrayList<byte[]> data)
    {
        ArrayList<byte[]> dateTime = new ArrayList<>();

        for (int i=0; i<data.size(); i++)
        {
            int records = 0;
            int leng = data.get(i).length;
            byte[] tmpDate = new byte[5];

            if (leng > 8)
                records = (leng - 8) / 3;
            Log.d(TAG, "data length: " + leng + ", records: " + records);

            for(int j=0; j<5; j++)
            {
                tmpDate[j] = data.get(i)[j];
            }

            for (int k=0; k<=records; k++)
            {
                tmpDate[4] += k;
                byte[] newTmepTime = tmpDate.clone();
                dateTime.add(newTmepTime);
            }
        }

        //--- debug message
        for (int i=0; i<dateTime.size(); i++)
            Log.d(TAG, "dateTime[ " + i + "]: " + getHexToString(dateTime.get(i)));

        return dateTime;
    }

    //int cnt=0;
    public static ArrayList<Integer> getTemperature(ArrayList<byte[]> data)
    {
        int size = data.size();
        ArrayList<Integer> tmplist = new ArrayList<>();
        Log.d(TAG, ", size: " + size );

        for(int i=0; i<size; i++)
        {
            int tmp = 0;
            int leng = data.get(i).length-5;
            Log.d(TAG, "getTemperature(), data[" + i +"], lengh: " + leng);

            for(int j=0; j<(leng/3); j++)
            {
                int idx= (j*3);
                tmp = byteToUnsignedInt(data.get(i)[5 + idx]) * 100 +
                        byteToUnsignedInt(data.get(i)[6+idx]);
                tmplist.add(tmp);
            }
        }

        Log.d(TAG, "getTemperature(), tmplist size:" + tmplist.size());

        //--- debug message
        for(int i=0; i<tmplist.size(); i++)
        {
            Log.d(TAG, "getTemperature(), tmplist[" + i + "]: " + tmplist.get(i));
        }

        return tmplist;
    }

    public static int byteToUnsignedInt(byte b)
    {
        return 0x00 << 24 | b & 0xff;
    }

    public static int countCS(byte[] data)
    {
        int tmpCS=0;
        for (int i=0; i<(data.length-1); i++)
        {
            int tmpInt = byteToUnsignedInt(data[i]);
            tmpCS += tmpInt;
            //Log.d(TAG, format("countCS(), [%d]: %04X, %04X", i, tmpInt, tmpCS));
        }
        Log.d(TAG, format("countCS(): %04X H", tmpCS));
        //System.out.println(String.format("countCS(): %04Xh", (tmpCS & 0x00ff)));
        return (tmpCS);
    }

    public static String convertArrayToString(byte[] data, int start, int length)
    {
        byte[] tmpbyte = new byte[length];

        for (int i=0; i<length; i++)
            tmpbyte[i] = data[start+i];
        String tmpStr = getHexToString(tmpbyte);

        return(tmpStr);
    }

    public static String getHexToString(byte[] raw)
    {
        //StringBuilder sb= new StringBuilder(responInfo.length);
        //for (byte indx: responInfo)
        //{
        //    sb.append(format("%02X", indx));
        //}

        if (raw == null)
        {
            return null;
        }

        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw)
        {
            hex.append(HEXES.charAt((b & 0xF0) >> 4))
                    .append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    public static byte[] hexStringToByteArray(String s)
    {
        int len = s.length();
        byte[] data = new byte[len/2];

        for(int i = 0; i < len; i+=2)
        {
            data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }

        return data;
    }

    public static String removeColon(String s)
    {
        String strArray[] = s.split(":");
        String tmpStr = "";

        Log.d("Colon", "string Array: " + strArray.toString());

        for (int i=0; i<strArray.length; i++)
        {
            tmpStr += strArray[i];
        }
        Log.d("Colon", "no Colon string: " + tmpStr);

        return tmpStr;
    }
}

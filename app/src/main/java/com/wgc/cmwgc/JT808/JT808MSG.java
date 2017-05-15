package com.wgc.cmwgc.JT808;

import android.util.Log;

import com.orhanobut.logger.Logger;
import com.wgc.cmwgc.JT808.util.BCD8421Operater;
import com.wgc.cmwgc.JT808.util.BitOperator;
import com.wgc.cmwgc.JT808.util.HexStringUtils;
import com.wgc.cmwgc.JT808.util.JT808ProtocolUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 功能： descriable
 * 作者： Administrator
 * 日期： 2017/3/31 17:52
 * 邮箱： descriable
 */
public class JT808MSG {

    private JT808ProtocolUtils  jt808ProtocolUtils;
    private BitOperator bitOperator;
    private BCD8421Operater bcd8421Operater;

    public JT808MSG(){
        jt808ProtocolUtils = new JT808ProtocolUtils();
        this.bitOperator = new BitOperator();
        this.bcd8421Operater = new BCD8421Operater();
    }

    /**
     * @return
     */
    public byte[] getRegisterInfo(String did,int flowId){
/*
        7E

        0100
        002E
        056625301412
        0002

        002C
        012F
        3730383636
        58432D4133 0000000000 0000000000 0000000000
        35333031343132
        01
        D4C1422E4B33423935
        5A
        7E

*/
        /*

        7E
        0100
        002A
        000000000000
        0000

        0000
        0000
        0000000000

        0000000000 0000000000 0000000000  0000000000
        32808539340000
        00
        0000000000
        11
        7E

        */


        byte[] cityid = new byte[2];
        byte[] townid = new byte[2];
        byte[] makeId = new byte[5];
        byte[] deviceId = new byte[7];
        byte[] deviceIdBytes = new byte[20];
        byte[] carColor = new byte[1];
        byte[] carBand = new byte[5];


        byte[] body = new byte[cityid.length + townid.length + makeId.length + deviceId.length + deviceIdBytes.length + carColor.length
                + carBand.length];

        /*  消息体   ----------把上面的 每个数组填到 body数组里面*/
        System.arraycopy(cityid,0,body,0,cityid.length);
        System.arraycopy(townid,0,body,cityid.length,townid.length);
        System.arraycopy(makeId,0,body,cityid.length+townid.length,makeId.length);

//        deviceId = bcd8421Operater.did2Bcd(did);
//        Log.d("BeiDouService",deviceId.length + " --- -  --　" + HexStringUtils.toHexString(deviceId));
        System.arraycopy(deviceId,0,body,29,deviceId.length);

//        System.arraycopy(did.getBytes(),0,deviceIdBytes,0,did.getBytes().length);

        System.arraycopy(deviceIdBytes,0,body,9,deviceIdBytes.length);
        System.arraycopy(carColor,0,body,36,carColor.length);
        System.arraycopy(carBand,0,body,37,carBand.length);

/*---------------------------------------------------------------------------------------------------------------------------*/
       /* 消息头*/
        int msgBodyProps = jt808ProtocolUtils.generateMsgBodyProps(body.length,0,false,0);
        byte[] head = null;
        try {
            head = jt808ProtocolUtils.generateMsgHeader(get12DeviceId(did), TPMSConsts.msg_id_terminal_register,msgBodyProps,flowId);
        } catch (Exception e) {
            e.printStackTrace();
        }
/*---------------------------------------------------------------------------------------------------------------------------*/

       /*消息头 和消息体组合拼接*/
        byte[] msg =  new byte[head.length + body.length];
        /*把消息体 和消息头 合并到 msg里面*/
        System.arraycopy(head, 0, msg, 0, head.length);
        System.arraycopy(body, 0, msg, head.length, body.length);

        byte[] checkCode = {jt808ProtocolUtils.generateCheckCode(msg)};



        byte[] identifierBit = {0x7e};

        byte [] regByte = new byte[msg.length + 3];



//        byte[] AA = {(byte) (bitOperator.getCheckSum4JT808(msg, 0, msg.length)&0xff)};

//        Log.d("BeiDouService",HexStringUtils.toHexString(AA) + " --- 验证码  --　" + HexStringUtils.toHexString(checkCode));

        /* 把 7e  msg[] 检验码  7e 拼接组成完整消息*/
        System.arraycopy(identifierBit, 0, regByte, 0, identifierBit.length);
        System.arraycopy(msg, 0, regByte, identifierBit.length, msg.length);
        System.arraycopy(checkCode, 0, regByte,msg.length + 1, checkCode.length);
        System.arraycopy(identifierBit, 0, regByte,msg.length + 2, identifierBit.length);

        byte [] r = null;
        try {
            r = jt808ProtocolUtils.doEscape4Send(regByte,1,regByte.length-2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("BeiDouService",r.length + " --- -  --　" + HexStringUtils.toHexString(r));
        return r;
    }

    /**
     * @param body 注册得到的鉴权码
     * @return
     */
    public byte [] getAuthenticationInfo(String did,byte[] body,int flowId){
        int msgBodyProps = jt808ProtocolUtils.generateMsgBodyProps(body.length,0,false,0);
        byte[] head = null;
        try {
            head = jt808ProtocolUtils.generateMsgHeader(get12DeviceId(did), TPMSConsts.msg_id_terminal_authentication,msgBodyProps,flowId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] msg =  new byte[head.length + body.length];
        System.arraycopy(head, 0, msg, 0, head.length);
        System.arraycopy(body, 0, msg, head.length, body.length);
        byte[] checkCode = {jt808ProtocolUtils.generateCheckCode(msg)};
        byte[] identifierBit = {0x7e};
        byte [] authByte = new byte[msg.length + 3];
        System.arraycopy(identifierBit, 0, authByte, 0, identifierBit.length);
        System.arraycopy(msg, 0, authByte, identifierBit.length, msg.length);
        System.arraycopy(checkCode, 0, authByte,msg.length + 1, checkCode.length);
        System.arraycopy(identifierBit, 0, authByte,msg.length + 2, identifierBit.length);
        byte [] r = null;
        try {
            r = jt808ProtocolUtils.doEscape4Send(authByte,1,authByte.length-2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }


    /**
     * 心跳包 每隔一段时间检查链路
     * @param flowId
     * @return
     */
    public byte[] getHeartBeat(String did,int flowId){
        int msgBodyProps = jt808ProtocolUtils.generateMsgBodyProps(0,0,false,0);
        byte[] head = null;
        try {
            head = jt808ProtocolUtils.generateMsgHeader(get12DeviceId(did), TPMSConsts.msg_id_terminal_heart_beat,msgBodyProps,flowId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] msg =  new byte[head.length];
        System.arraycopy(head, 0, msg, 0, head.length);
        byte[] checkCode = {jt808ProtocolUtils.generateCheckCode(msg)};
        byte[] identifierBit = {0x7e};
        byte [] authByte = new byte[msg.length + 3];

        System.arraycopy(identifierBit, 0, authByte, 0, identifierBit.length);
        System.arraycopy(msg, 0, authByte, identifierBit.length, msg.length);
        System.arraycopy(checkCode, 0, authByte,msg.length + 1, checkCode.length);
        System.arraycopy(identifierBit, 0, authByte,msg.length + 2, identifierBit.length);
        byte [] r = null;
        try {
            r = jt808ProtocolUtils.doEscape4Send(authByte,1,authByte.length-2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }


    /**
     * @param alert
     * @param status
     * @param lon
     * @param lat
     * @param heigh
     * @param speed
     * @param direction
     * @param time
     * @param flowId
     * @return
     */
    public byte[] getLocation(String did,int alert,int status,int lon,int lat,int heigh,int speed,int mileage, int direction,String time,int flowId){
       /* 消息体 */
        byte[] alertFlay = new byte[4];
        alertFlay = bitOperator.integerTo4Bytes(alert);
        byte[] gpsStatus = new byte[4];
        gpsStatus = bitOperator.integerTo4Bytes(status);
        byte[] gpsLon = new byte[4];
        gpsLon = bitOperator.integerTo4Bytes(lon);
        byte[] gpsLat = new byte[4];
        gpsLat = bitOperator.integerTo4Bytes(lat);
        byte[] gpsHeigh = new byte[2];
        gpsHeigh = bitOperator.integerTo2Bytes(heigh);
        byte[] gpsSpeed = new byte[2];
        gpsSpeed = bitOperator.integerTo2Bytes(speed * 10);
        byte[] gpsDirection = new byte[2];
        gpsDirection = bitOperator.integerTo2Bytes(direction);
        byte[] gpsMileage = new byte[4];
        gpsMileage = bitOperator.integerTo4Bytes(mileage * 10);

        Log.d("BeiDouService","---- " + time);
        byte[] gpsTime = new byte[6];
        gpsTime = bcd8421Operater.string2Bcd(time);

        byte [] body = new byte[alertFlay.length+gpsStatus.length+gpsLon.length+gpsLat.length+gpsHeigh.length+gpsSpeed.length+2+gpsMileage.length+gpsDirection.length+gpsTime.length];
        System.arraycopy(alertFlay, 0, body, 0, alertFlay.length);
        System.arraycopy(gpsStatus, 0, body, 4, gpsStatus.length);

        System.arraycopy(gpsLat, 0, body, 8, gpsLat.length);
        System.arraycopy(gpsLon, 0, body, 12, gpsLon.length);
        System.arraycopy(gpsHeigh, 0, body, 16, gpsHeigh.length);
        System.arraycopy(gpsSpeed, 0, body, 18, gpsSpeed.length);
        System.arraycopy(gpsDirection, 0, body, 20, gpsDirection.length);
        System.arraycopy(gpsTime, 0, body, 22, gpsTime.length);
        byte[] a = new byte[1];
        byte[] b = new byte[1];
        a[0] = 1;
        b[0] = 4;
        System.arraycopy(a, 0, body, 28, 1);
        System.arraycopy(b, 0, body, 29, 1);
        System.arraycopy(gpsMileage, 0, body, 30, 4);
        Log.e("数据--------------",body+"");
       /*
        7E
        02 00
        00 1C
        43 28 08 53 93 41
        01 CF

        00 00 00 00
        00 00 00 00
        06 CA 2F 14
        01 58 A3 07
        00 00
        00 00
        00 00
        17 04 07 12 01 19
        26
        7E
*/

/*
        7e
        02 00
        00 30
        05 66 25 30 14 12
        02 c2

        00 00 00 00

        00 00 00 00

        01 58 98 7d
        01 06 ca 44

        46 00 00 00 00 00 c0 16 10 09 12 12 46 30 01 19 31 01 00 e1 04 00 00 00 3b e2 02 00 03 e3 02 00 63 de 7e

        */



//        ByteArrayOutputStream body = null;
//        try {
//            body = new ByteArrayOutputStream();
//            // 1. alertFlag word(32)  4
//            body.write(bitOperator.integerTo4Bytes(alert));
//            // 2. status word(32)   4
//            body.write(bitOperator.integerTo4Bytes(status));
//            // 3. lon(32)  4
//            body.write(bitOperator.integerTo4Bytes(lon));
////            Log.d("BeiDouService", " lon - " + HexStringUtils.toHexString(bitOperator.integerTo4Bytes(lon)));
//            // 4. lat(32)  4
//            body.write(bitOperator.integerTo4Bytes(lat));
////            Log.d("BeiDouService", " lat - " + HexStringUtils.toHexString(bitOperator.integerTo4Bytes(lat)));
//
//
//            // 5. heigh(16)  2
//            body.write(bitOperator.integerTo2Bytes(heigh));
//            // 6. speed(16)  2
//            body.write(bitOperator.integerTo2Bytes(speed));
//            // 7. direction(16)  2
//            body.write(bitOperator.integerTo2Bytes(direction));

            // 8. time[6]
//            body.write(bcd8421Operater.string2Bcd(time));
//            Log.d("BeiDouService", " time - " + HexStringUtils.toHexString(bcd8421Operater.string2Bcd(time)));


//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (body != null) {
//                try {
//                    body.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        /* 消息头的消息体属性 */
        int msgBodyProps = jt808ProtocolUtils.generateMsgBodyProps(body.length,0,false,0);
        byte[] head = null;
        try {
            head = jt808ProtocolUtils.generateMsgHeader(get12DeviceId(did), TPMSConsts.msg_id_terminal_location_info_upload,msgBodyProps,flowId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] msg =  new byte[head.length + body.length];
        System.arraycopy(head, 0, msg, 0, head.length);
        System.arraycopy(body, 0, msg, head.length, body.length);
        byte[] checkCode = {jt808ProtocolUtils.generateCheckCode(msg)};





        byte[] identifierBit = {0x7e};
        byte [] authByte = new byte[msg.length + 3];
        System.arraycopy(identifierBit, 0, authByte, 0, identifierBit.length);
        System.arraycopy(msg, 0, authByte, identifierBit.length, msg.length);
        System.arraycopy(checkCode, 0, authByte,msg.length + 1, checkCode.length);
        System.arraycopy(identifierBit, 0, authByte,msg.length + 2, identifierBit.length);
        byte [] r = null;
        try {
            r = jt808ProtocolUtils.doEscape4Send(authByte,1,authByte.length-2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }




    int didLengh =12;
    private String get12DeviceId(String str){
        if(str.length()>didLengh){
            str = str.substring(str.length()-didLengh,str.length());
        }
        if (str.length() < didLengh){
            int lengh = didLengh-str.length();
            for (int i=0;i<lengh;i++){
                str = "0" +str;
            }
        }
        return str;
    }






























    /**
     * Convert hex string to byte[]
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            Logger.w( "--------" + d[i]);
        }
        return d;
    }

    /**
     * Convert char to byte
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }



    public byte[] getMsg(byte[] bs, int start, int end) throws Exception {
        if (start < 0 || end > bs.length)
            throw new ArrayIndexOutOfBoundsException("doEscape4Receive error : index out of bounds(start=" + start
                    + ",end=" + end + ",bytes length=" + bs.length + ")");
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            for (int i = start; i < end ; i++) {
                baos.write(bs[i]);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            throw e;
        } finally {
            if (baos != null) {
                baos.close();
                baos = null;
            }
        }
    }
}

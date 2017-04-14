package com.wgc.cmwgc.JT808;

import android.util.Log;

import com.wgc.cmwgc.JT808.util.BCD8421Operater;
import com.wgc.cmwgc.JT808.util.BitOperator;
import com.wgc.cmwgc.JT808.util.HexStringUtils;
import com.wgc.cmwgc.JT808.util.JT808ProtocolUtils;

import java.io.ByteArrayOutputStream;

/**
 * 功能： descriable
 * 作者： Administrator
 * 日期： 2017/3/31 16:03
 * 邮箱： descriable
 */
public class MsgDecoder {

    private BitOperator bitOperator;
    private BCD8421Operater bcd8421Operater;
    private JT808ProtocolUtils jt808ProtocolUtils;

    public MsgDecoder() {
        jt808ProtocolUtils = new JT808ProtocolUtils();
        this.bitOperator = new BitOperator();
        this.bcd8421Operater = new BCD8421Operater();
    }

    //字节数组到消息体实体类
    public PackageData queueElement2PackageData(byte[] buff) {
        /*8100 0005 432808539341 7D01E9 0000005152A6*/
        PackageData ret = new PackageData();
        byte[] data = new byte[buff.length];
        try {
            data = jt808ProtocolUtils.doEscape4Receive(buff,0,buff.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Log.e("BeiDouService"," --- -  消息体  ！" + HexStringUtils.toHexString(data));

        // 1. 16byte 或 12byte 消息头
        PackageData.MsgHeader msgHeader = this.parseMsgHeaderFromBytes(data);
        ret.setMsgHeader(msgHeader);

        int msgBodyByteStartIndex = 12;
        // 2. 消息体
        // 有子包信息,消息体起始字节后移四个字节:消息包总数(word(16))+包序号(word(16))
        if (msgHeader.isHasSubPackage()) {
            msgBodyByteStartIndex = 16;
        }

        byte[] tmp = new byte[msgHeader.getMsgBodyLength()];
        System.arraycopy(data, msgBodyByteStartIndex, tmp, 0, tmp.length);

        Log.e("BeiDouService"," --- -  消息体  ！" + HexStringUtils.toHexString(tmp));
        ret.setMsgBodyBytes(tmp);

//        PackageData.MsgBody msgBody = new PackageData.MsgBody();
//        msgBody.setFlowId(this.parseIntFromBytes(tmp, 0, 2));
//        msgBody.setResult(this.parseIntFromBytes(tmp, 2, 1));
//        if(tmp.length>3){
//            byte[] msg = new byte[tmp.length-3];
//            System.arraycopy(tmp, 3, msg, 0, msg.length);
//            msgBody.setMsgData(msg);
//
//        }
//        ret.setMsgBody(msgBody);

        // 3. 去掉分隔符之后，最后一位就是校验码
        // int checkSumInPkg =
        // this.bitOperator.oneByteToInteger(data[data.length - 1]);

//        byte[] aa = null;
//        try {
//            aa = jt808ProtocolUtils.doEscape4Receive(buff,0,buff.length-1);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        int checkSumInPkg = data[data.length - 1];
//        try {
//            Log.e("BeiDouService",jt808ProtocolUtils.generateCheckCode(getMsg(buff,0,buff.length-1)) + " --- -  校验码  ！" + checkSumInPkg);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        int calculatedCheckSum = this.bitOperator.getCheckSum4JT808(data, 0, data.length - 1);
        ret.setCheckSum(checkSumInPkg);
        if (checkSumInPkg != calculatedCheckSum) {
            Log.w("" , "检验码不一致,msgid:{},pkg:{},calculated:{}"+ " -- " + msgHeader.getMsgId() + " -- " + checkSumInPkg + " -- " + calculatedCheckSum);
        }
        return ret;
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

    private PackageData.MsgHeader parseMsgHeaderFromBytes(byte[] data) {
        PackageData.MsgHeader msgHeader = new PackageData.MsgHeader();

        // 1. 消息ID word(16)
        // byte[] tmp = new byte[2];
        // System.arraycopy(data, 0, tmp, 0, 2);
        // msgHeader.setMsgId(this.bitOperator.twoBytesToInteger(tmp));
        msgHeader.setMsgId(this.parseIntFromBytes(data, 0, 2));

        // 2. 消息体属性 word(16)=================>
        // System.arraycopy(data, 2, tmp, 0, 2);
        // int msgBodyProps = this.bitOperator.twoBytesToInteger(tmp);
        int msgBodyProps = this.parseIntFromBytes(data, 2, 2);
        msgHeader.setMsgBodyPropsField(msgBodyProps);
        // [ 0-9 ] 0000,0011,1111,1111(3FF)(消息体长度)
        msgHeader.setMsgBodyLength(msgBodyProps & 0x1ff);
        // [10-12] 0001,1100,0000,0000(1C00)(加密类型)
        msgHeader.setEncryptionType((msgBodyProps & 0xe00) >> 10);
        // [ 13_ ] 0010,0000,0000,0000(2000)(是否有子包)
        msgHeader.setHasSubPackage(((msgBodyProps & 0x2000) >> 13) == 1);
        // [14-15] 1100,0000,0000,0000(C000)(保留位)
        msgHeader.setReservedBit(((msgBodyProps & 0xc000) >> 14) + "");
        // 消息体属性 word(16)<=================

        // 3. 终端手机号 bcd[6]
        // tmp = new byte[6];
        // System.arraycopy(data, 4, tmp, 0, 6);
        // msgHeader.setTerminalPhone(this.bcd8421Operater.bcd2String(tmp));
        msgHeader.setTerminalPhone(this.parseBcdStringFromBytes(data, 4, 6));

        // 4. 消息流水号 word(16) 按发送顺序从 0 开始循环累加
        // tmp = new byte[2];
        // System.arraycopy(data, 10, tmp, 0, 2);
        // msgHeader.setFlowId(this.bitOperator.twoBytesToInteger(tmp));
        msgHeader.setFlowId(this.parseIntFromBytes(data, 10, 2));

        // 5. 消息包封装项
        // 有子包信息
        if (msgHeader.isHasSubPackage()) {
            // 消息包封装项字段
            msgHeader.setPackageInfoField(this.parseIntFromBytes(data, 12, 4));
            // byte[0-1] 消息包总数(word(16))
            // tmp = new byte[2];
            // System.arraycopy(data, 12, tmp, 0, 2);
            // msgHeader.setTotalSubPackage(this.bitOperator.twoBytesToInteger(tmp));
            msgHeader.setTotalSubPackage(this.parseIntFromBytes(data, 12, 2));

            // byte[2-3] 包序号(word(16)) 从 1 开始
            // tmp = new byte[2];
            // System.arraycopy(data, 14, tmp, 0, 2);
            // msgHeader.setSubPackageSeq(this.bitOperator.twoBytesToInteger(tmp));
            msgHeader.setSubPackageSeq(this.parseIntFromBytes(data, 12, 2));
        }
        return msgHeader;
    }

    protected String parseStringFromBytes(byte[] data, int startIndex, int lenth) {
        return this.parseStringFromBytes(data, startIndex, lenth, null);
    }

    private String parseStringFromBytes(byte[] data, int startIndex, int lenth, String defaultVal) {
        try {
            byte[] tmp = new byte[lenth];
            System.arraycopy(data, startIndex, tmp, 0, lenth);
            return new String(tmp, "UTF-8");
        } catch (Exception e) {
            Log.e("解析字符串出错:{}", e.getMessage());
            e.printStackTrace();
            return defaultVal;
        }
    }

    private String parseBcdStringFromBytes(byte[] data, int startIndex, int lenth) {
        return this.parseBcdStringFromBytes(data, startIndex, lenth, null);
    }

    private String parseBcdStringFromBytes(byte[] data, int startIndex, int lenth, String defaultVal) {
        try {
            byte[] tmp = new byte[lenth];
            System.arraycopy(data, startIndex, tmp, 0, lenth);
            return this.bcd8421Operater.bcd2String(tmp);
        } catch (Exception e) {
            Log.e("解析BCD(8421码)出错:{}", e.getMessage());
            e.printStackTrace();
            return defaultVal;
        }
    }

    private int parseIntFromBytes(byte[] data, int startIndex, int length) {
        return this.parseIntFromBytes(data, startIndex, length, 0);
    }

    private int parseIntFromBytes(byte[] data, int startIndex, int length, int defaultVal) {
        try {
            // 字节数大于4,从起始索引开始向后处理4个字节,其余超出部分丢弃
            final int len = length > 4 ? 4 : length;
            byte[] tmp = new byte[len];
            System.arraycopy(data, startIndex, tmp, 0, len);
            return bitOperator.byteToInteger(tmp);
        } catch (Exception e) {
            Log.e("解析整数出错:{}", e.getMessage());
            e.printStackTrace();
            return defaultVal;
        }
    }
}

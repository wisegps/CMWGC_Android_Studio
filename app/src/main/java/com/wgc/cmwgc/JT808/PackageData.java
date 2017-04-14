package com.wgc.cmwgc.JT808;

import java.nio.channels.Channel;

/**
 * 功能： descriable
 * 作者： Administrator
 * 日期： 2017/3/31 16:02
 * 邮箱： descriable
 */
public class PackageData {

    /**
     * 16byte 消息头
     */
    protected MsgHeader msgHeader;

//    public MsgBody getMsgBody() {
//        return msgBody;
//    }
//
//    public void setMsgBody(MsgBody msgBody) {
//        this.msgBody = msgBody;
//    }
//
//    protected MsgBody msgBody;

    // 消息体字节数组
    protected byte[] msgBodyBytes;

    /**
     * 校验码 1byte
     */
    protected int checkSum;

    //记录每个客户端的channel,以便下发信息给客户端
    protected Channel channel;


    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setMsgHeader(MsgHeader msgHeader) {
        this.msgHeader = msgHeader;
    }

    public byte[] getMsgBodyBytes() {
        return msgBodyBytes;
    }

    public void setMsgBodyBytes(byte[] msgBodyBytes) {
        this.msgBodyBytes = msgBodyBytes;
    }

    public int getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(int checkSum) {
        this.checkSum = checkSum;
    }

    public MsgHeader getMsgHeader() {
        return msgHeader;
    }


//    public static class MsgBody{
//        // 流水号
//        protected int flowId;
//        protected int result;
//
//        public byte[] getMsgData() {
//            return msgData;
//        }
//
//        public void setMsgData(byte[] msgData) {
//            this.msgData = msgData;
//        }
//
//        public int getFlowId() {
//            return flowId;
//        }
//
//        public void setFlowId(int flowId) {
//            this.flowId = flowId;
//        }
//
//        public int getResult() {
//            return result;
//        }
//
//        public void setResult(int result) {
//            this.result = result;
//        }
//
//        protected byte[] msgData;
//
//    }







    //消息头
    public static class MsgHeader {
        // 消息ID
        protected int msgId;

        /////// ========消息体属性
        // byte[2-3]
        protected int msgBodyPropsField;
        // 消息体长度
        protected int msgBodyLength;
        // 数据加密方式
        protected int encryptionType;
        // 是否分包,true==>有消息包封装项
        protected boolean hasSubPackage;
        // 保留位[14-15]
        protected String reservedBit;
        /////// ========消息体属性

        // 终端手机号
        protected String terminalPhone;
        // 流水号
        protected int flowId;

        //////// =====消息包封装项
        // byte[12-15]
        protected int packageInfoField;
        // 消息包总数(word(16))
        protected long totalSubPackage;

        // 包序号(word(16))这次发送的这个消息包是分包中的第几个消息包, 从 1 开始
        protected long subPackageSeq;
        //////// =====消息包封装项



        public long getSubPackageSeq() {
            return subPackageSeq;
        }

        public void setSubPackageSeq(long subPackageSeq) {
            this.subPackageSeq = subPackageSeq;
        }

        public int getMsgId() {
            return msgId;
        }

        public void setMsgId(int msgId) {
            this.msgId = msgId;
        }

        public int getMsgBodyPropsField() {
            return msgBodyPropsField;
        }

        public void setMsgBodyPropsField(int msgBodyPropsField) {
            this.msgBodyPropsField = msgBodyPropsField;
        }

        public int getMsgBodyLength() {
            return msgBodyLength;
        }

        public void setMsgBodyLength(int msgBodyLength) {
            this.msgBodyLength = msgBodyLength;
        }

        public int getEncryptionType() {
            return encryptionType;
        }

        public void setEncryptionType(int encryptionType) {
            this.encryptionType = encryptionType;
        }

        public boolean isHasSubPackage() {
            return hasSubPackage;
        }

        public void setHasSubPackage(boolean hasSubPackage) {
            this.hasSubPackage = hasSubPackage;
        }

        public String getReservedBit() {
            return reservedBit;
        }

        public void setReservedBit(String reservedBit) {
            this.reservedBit = reservedBit;
        }

        public String getTerminalPhone() {
            return terminalPhone;
        }

        public void setTerminalPhone(String terminalPhone) {
            this.terminalPhone = terminalPhone;
        }

        public int getFlowId() {
            return flowId;
        }

        public void setFlowId(int flowId) {
            this.flowId = flowId;
        }

        public int getPackageInfoField() {
            return packageInfoField;
        }

        public void setPackageInfoField(int packageInfoField) {
            this.packageInfoField = packageInfoField;
        }

        public long getTotalSubPackage() {
            return totalSubPackage;
        }

        public void setTotalSubPackage(long totalSubPackage) {
            this.totalSubPackage = totalSubPackage;
        }



    }
}

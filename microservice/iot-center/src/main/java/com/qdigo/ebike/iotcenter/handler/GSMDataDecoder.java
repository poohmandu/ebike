/*
 * Copyright 2020 聂钊 nz@qdigo.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qdigo.ebike.iotcenter.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ByteProcessor;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by niezhao on 2017/8/17.
 */
@Slf4j
public final class GSMDataDecoder extends ByteToMessageDecoder {

    private final static AtomicInteger count = new AtomicInteger(0);
    private final static ByteProcessor FIND_END = new ByteProcessor.IndexOfProcessor((byte) '$');

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        try {
            Object decoded = this.decode(ctx, in);
            if (decoded != null) {
                out.add(decoded);
            }
        } finally {
            //io.netty.util.IllegalReferenceCountException
            ReferenceCountUtil.release(in);//父类ByteToMessageDecoder做了响应的释放
        }
    }

    private Object decode(ChannelHandlerContext ctx, ByteBuf in) {
        ByteBuf frame = this.parseByteBuf(in);
        if (frame == null) {
            log.info("数据被丢弃");
            return null;
        }
        byte[] bytes = new byte[frame.readableBytes()];
        frame.readBytes(bytes);

        log.info("接收请求数count={},ip:{},原始数据:\n{}", count.incrementAndGet(), ctx.channel().remoteAddress(), Arrays.toString(bytes));

        return bytes;
    }


    private ByteBuf parseByteBuf(ByteBuf in) {
        byte header0 = in.getByte(in.readerIndex());
        byte header1 = in.getByte(in.readerIndex() + 1);
        boolean headerOk = ('P' == header0 &&
                ('G' == header1 || 'H' == header1 || 'L' == header1 || 'C' == header1 || 'X' == header1))
                || ('M' == header0 &&
                ('D' == header1 || 'L' == header1 || 'C' == header1 || 'X' == header1));
        if (!headerOk) {
            //todo 可拓展: 再依次寻找'P' 记录位置，将它作为指针0，再解析一次
            log.info("检测到包头(header)不合法,为{},{}", header0, header1);
            return null;
        } else {
            int length;
            if ('C' == header1 || 'X' == header1) {
                //PC PX MC MX
                int imei_seq_cmd_index = in.readerIndex() + 8;
                int ed = in.forEachByte(imei_seq_cmd_index, in.writerIndex() - imei_seq_cmd_index, FIND_END);
                //int ed = in.forEachByte(FIND_END); 这种几个连一起不行
                length = ed - in.readerIndex();
            } else {
                if ('P' == header0) {
                    if ('G' == header1) {
                        length = 22;//PG
                    } else if ('H' == header1) {
                        length = 24;//PH
                    } else {
                        length = 17;//PL
                    }
                } else {
                    if ('D' == header1) {
                        length = 16;//MD
                    } else {
                        length = 20;//ML
                    }
                }
            }
            byte end = in.getByte(in.readerIndex() + length);

            // 其实这一步只是多余的验证,可有可无
            if (end != '$') {
                log.info("检测到包尾(end)不合法,{},{}", end, (byte) '$');
                return null;
            } else {
                ByteBuf frame = in.readRetainedSlice(length);
                in.skipBytes(1);
                return frame;
            }
        }

    }


}

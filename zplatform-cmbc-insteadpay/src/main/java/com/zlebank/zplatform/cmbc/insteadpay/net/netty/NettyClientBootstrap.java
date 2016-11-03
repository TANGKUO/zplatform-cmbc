/* 
 * NettyClientBootstrap.java  
 * 
 * version TODO
 *
 * 2016年11月2日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.insteadpay.net.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年11月2日 上午8:50:49
 * @since
 */
public class NettyClientBootstrap {

	private static final Logger log = LoggerFactory.getLogger(NettyClientBootstrap.class);
	private int port;
	private String host;
	private volatile boolean running = false;
	private long lastSendTime;
	private SocketChannel socketChannel;
	private static final EventExecutorGroup group = new DefaultEventExecutorGroup(20);

	private static NettyClientBootstrap bootstrap = null;

	public static synchronized NettyClientBootstrap getInstance(String host,
			int port) throws InterruptedException {
		if (bootstrap == null) {
			log.info("本地[{}:{}]", host,port);
			bootstrap = new NettyClientBootstrap(port, host);
			bootstrap.start();
		} else {
			if (bootstrap.socketChannel == null) {
				bootstrap.start();
			} else {
				if (!bootstrap.socketChannel.isActive()) {
					bootstrap.shutdown();
					bootstrap.start();
				}
			}
		}
		return bootstrap;
	}

	public NettyClientBootstrap(int port, String host) {
		this.port = port;
		this.host = host;
	}

	private void start() throws InterruptedException {
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.group(eventLoopGroup);
		bootstrap.remoteAddress(host, port);
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel socketChannel)
					throws Exception {
				socketChannel.pipeline().addLast(new ByteArrayDecoder());
				socketChannel.pipeline().addLast(new ByteArrayEncoder());
				socketChannel.pipeline().addLast(new NettyClientHandler());
			}
		});
		ChannelFuture future = bootstrap.connect(host, port).sync();

		if (future.isSuccess()) {
			socketChannel = (SocketChannel) future.channel();
			InetSocketAddress localAddress = socketChannel.localAddress();
			log.info("本地{}:{}-->{}:{} 连接成功",
					new Object[] { localAddress.getAddress().getHostAddress(),
							localAddress.getPort(), host, port });
			/*System.out.println(localAddress.getAddress().getHostAddress() + ":"
					+ localAddress.getPort());*/
			SocketChannelHelper channelHelper = SocketChannelHelper.getInstance();
			channelHelper.setLastActiveTime(new Date());
			channelHelper.setSocketKey(localAddress.getAddress().getHostAddress()+":"+localAddress.getPort());
			running = true;
			new Thread(new KeepAliveWatchDog()).start();
		}
	}
	
	public void sendMessage(byte[] msg){
		if(!socketChannel.isActive()){
			shutdown();
			try {
				start();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		socketChannel.writeAndFlush(msg);
	}
	
	private void shutdown(){
		if(socketChannel!=null){
			socketChannel.close();
			socketChannel = null;
			running = false;
			log.info("本地[{}]关闭",SocketChannelHelper.getInstance().getSocketKey());
		}
	}

	
	
	class KeepAliveWatchDog implements Runnable {
        public KeepAliveWatchDog(){
            Thread.currentThread().setName("InsteadPay KeepAliveWatchDog Thread");
            log.info("InsteadPay KeepAliveWatchDog Thread start");
        }
        //long checkDelay = 0;
        long keepAliveDelay = 30;
        public void run() {
            while (running) {
                if (System.currentTimeMillis() - lastSendTime > keepAliveDelay) {
                    log.info("send hert messageas");
					try {
						if(socketChannel.isActive()){
							socketChannel.writeAndFlush("00000000");
						}else{
							NettyClientBootstrap.this.shutdown();
							break;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						NettyClientBootstrap.this.shutdown();
					}
                    lastSendTime = System.currentTimeMillis();
                } else {
                    try {
                        TimeUnit.SECONDS.sleep(keepAliveDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        NettyClientBootstrap.this.shutdown();
                    }
                }
            }
            log.info("KeepAliveWatchDog running:"+running);
        }
    }
}

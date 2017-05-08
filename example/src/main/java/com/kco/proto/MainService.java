//package com.kco.proto;
//
//import com.kco.proto.demo2.GreeterImpl;
//import io.grpc.Server;
//import io.grpc.ServerBuilder;
//import io.grpc.netty.NettyServerBuilder;
//import proto.GreeterServiceGrpc;
//
///**
// * Created by Administrator on 2017/5/5.
// */
//public class MainService {
//    private int port = 50051;
//    private Server server;
//
//    public void start() throws Exception{
//        server = NettyServerBuilder.forPort(port)
//                .addService(new GreeterImpl()).build();
//        server.start();
//
//        Runtime.getRuntime().addShutdownHook(new Thread(){
//            @Override
//            public void run() {
//                System.out.println("*** shutting down gRPC server since JVM is shutting dowm");
//                server.shutdown();
//                System.out.println("*** server shut down");
//            }
//        });
//        server.awaitTermination();
//    }
//
//    public static void main(String[] args) throws Exception {
//        new MainService().start();
//    }
//}

//package com.kco.proto;
//
//import io.grpc.ManagedChannel;
//import io.grpc.ManagedChannelBuilder;
//import proto.GreeterServiceGrpc;
//import proto.Helloworld;
//
///**
// * Created by Administrator on 2017/5/5.
// */
//public class Client {
//    public static void main(String[] args) {
//        ManagedChannel channel = ManagedChannelBuilder.forAddress("127.0.0.1", 50051)
//                .usePlaintext(true)
//                .build();
//        GreeterServiceGrpc.GreeterServiceBlockingStub stub = GreeterServiceGrpc.newBlockingStub(channel);
//        Helloworld.HelloRequest request = Helloworld.HelloRequest.newBuilder()
//                .setName("Jack").build();
//        Helloworld.HelloReply reply = stub.sayHello(request);
//        Helloworld.HelloReply reply2 = stub.sayHello(request);
//        System.out.println(reply);
//        System.out.println(reply2);
//        channel.shutdown();
//
//    }
//}

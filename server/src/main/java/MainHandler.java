import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MainHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof FileRequest) {
                FileRequest fr = (FileRequest) msg;
                System.out.println("Запрос файла " + fr.getFilename() + " с клиента.");

                if (Files.exists(Paths.get("server_storage/" + fr.getFilename()))) {
                    FileMessage fm = new FileMessage(Paths.get("server_storage/" + fr.getFilename()));
                    System.out.println("Отправка файла " + fm.getFilename() + " на клиент.");
                    ctx.writeAndFlush(fm);
                }
            }
            else if (msg instanceof FileMessage){
                FileMessage fm = (FileMessage) msg;
                System.out.println("Пришел файл " + fm.getFilename() + " с клиента.");

                Files.write(Paths.get("server_storage/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
            }
            else if (msg instanceof FilesListRequest){
                FilesListRequest flr = (FilesListRequest) msg;
                System.out.println("Запрос списка файлов пользователя ID: " + flr.getClientId() + " с клиента.");
                ctx.writeAndFlush(new FilesListMessage(flr.getClientId()));
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

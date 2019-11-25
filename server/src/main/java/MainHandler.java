import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public class MainHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof FileRequest) {
                FileRequest fr = (FileRequest) msg;
                System.out.println("Запрос файла " + fr.getFilename() + " с клиента.");

                if (Files.exists(Paths.get("server_storage/"+fr.getClientId()+"/" + fr.getFilename()))) {
                    FileMessage fm = new FileMessage(fr.getClientId(),Paths.get("server_storage/"+fr.getClientId()+"/" + fr.getFilename()));
                    System.out.println("Отправка файла " + fm.getFilename() + " на клиент ID: " + fr.getClientId());
                    ctx.writeAndFlush(fm);
                }
            }
            else if (msg instanceof FileMessage){
                FileMessage fm = (FileMessage) msg;
                System.out.println("Пришел файл " + fm.getFilename() + " с клиента ID: " + fm.getClientId());
                Files.write(Paths.get("server_storage/"+fm.getClientId()+"/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);

                ctx.writeAndFlush(new FilesListMessage(fm.getClientId())); //Отправка Обновленного листа файлов с сервера
            }
            else if (msg instanceof FilesListRequest){
                FilesListRequest flr = (FilesListRequest) msg;
                System.out.println("Запрос списка файлов пользователя ID: " + flr.getClientId() + " с клиента.");
                ctx.writeAndFlush(new FilesListMessage(flr.getClientId()));
            }
            else if (msg instanceof FileDeleteRequest){
                FileDeleteRequest fdr = (FileDeleteRequest) msg;
                System.out.println("Удаление списка файлов от пользователя: " + fdr.getClientId() + " с клиента.");

                Files.deleteIfExists(Paths.get("server_storage/"+fdr.getClientId()+"/" + fdr.getFilename()));

                ctx.writeAndFlush(new FilesListMessage(fdr.getClientId()));
            }
            else if (msg instanceof AbstractMessage){ //Авторизация
                AuthMessage am = (AuthMessage) msg;
                System.out.println("С клиента пришел запрос на аторизацию. login: " + am.getLoginFiled() + ", psw: " + am.getPasswordField());
                if (am.getLoginFiled()!=null){
                    UUID clientId = AuthServ.getIdByLogPass(am.getLoginFiled(), am.getPasswordField());
                    String clientName = AuthServ.getNameByLogPass(am.getLoginFiled(), am.getPasswordField());
                    //System.out.println("id client: " + clientId);

                    AuthMessageReq amr = null;

                    if (clientId != null)  amr = new AuthMessageReq(true,clientId,clientName);
                    else  amr = new AuthMessageReq(false,clientId,clientName);
                    ctx.writeAndFlush(amr);
                    ctx.writeAndFlush(new FilesListMessage(clientId));
                };

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

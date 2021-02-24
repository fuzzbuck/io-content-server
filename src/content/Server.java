package content;

import arc.util.Log;
import arc.util.serialization.Base64Coder;
import express.Express;
import express.utils.MediaType;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Base64;

public class Server {
    public static void init(int port){
        Express app = new Express();

        app.get("/", (req, res) -> {
            res.send("yes im still very much here");
        });

        app.post("/readmap", (req, res) -> {
            InputStream data = req.getBody();

            try {
                Log.info(data.available());
                ContentHandler.Map map = main.handler.readMap(data);

                Log.info(map.author);
                Log.info(map.description);

                res.setContentType(MediaType._json);

                File imgFile = new File("iocontent/" + main.randomString(10) + ".png");
                ImageIO.write(map.image, "png", imgFile);

                res.setHeader("author", Base64Coder.encodeString(map.author));
                res.setHeader("desc", Base64Coder.encodeString(map.description));
                res.setHeader("size", Base64Coder.encodeString(map.image.getWidth() + "x" + map.image.getHeight()));
                res.setHeader("name", Base64Coder.encodeString(map.name));

                res.sendAttachment(imgFile.toPath());
            } catch (Exception e) {
                e.printStackTrace();
                res.send("error");
            }
        });

        app.bind("127.0.0.1");
        app.listen(port);
    }
}

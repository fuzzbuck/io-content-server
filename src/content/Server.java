package content;

import arc.files.Fi;
import arc.util.Log;
import arc.util.serialization.Base64Coder;
import express.Express;
import express.utils.MediaType;
import express.utils.Status;
import mindustry.game.Schematic;
import mindustry.game.Schematics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Base64;
import java.util.Scanner;

public class Server {
    public static void init(int port) {
        Express app = new Express();

        app.get("/", (req, res) -> {
            res.send("yes im still very much here\nHosting Maps and Schematics.");
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
                res.setStatus(Status.valueOf(400));
                res.send("error");
            }
        });

        app.post("/schematicpreview", (req, res) -> {
            InputStream data = req.getBody();
            try {
                Scanner reader = new Scanner(data);
                String request = reader.hasNext() ? reader.next() : "";
                Schematic schem;
                if (request.startsWith(ContentHandler.schemHeader)) {
                    System.out.println("Converting text schematic.");
                    schem = main.handler.parseSchematic(request);
                } else if (request.startsWith("https://")) {
                    System.out.println("Converting file schematic.");
                    schem = main.handler.parseSchematicURL(request);
                } else {
                    res.setStatus(Status.valueOf(400));
                    res.send("bad request");
                    return;
                }

                res.setContentType(MediaType._json);
                BufferedImage preview = main.handler.previewSchematic(schem);
                String sname = schem.name().replaceAll("[/ ]", "_");

                System.out.println(sname);

                String id = main.randomString(10);
                File imgFile = new File("iocontent/" + id + "_msch.png");
                File schemFile = new File("iocontent/" + id + ".msch");
                ImageIO.write(preview, "png", imgFile);
                Schematics.write(schem, new Fi(schemFile));

                res.setHeader("name", Base64Coder.encodeString(sname));
                res.setHeader("desc", Base64Coder.encodeString(schem.description()));
                res.setHeader("item", Base64Coder.encodeString(schem.requirements().toString()));
                res.setHeader("power_in", Base64Coder.encodeString(String.valueOf((int) (schem.powerProduction() * 60))));
                res.setHeader("power_out", Base64Coder.encodeString(String.valueOf((int) (schem.powerConsumption() * 60))));
                res.setHeader("requestid", Base64Coder.encodeString(id));

                res.sendAttachment(imgFile.toPath());
            } catch (Exception e) {
                e.printStackTrace();
                res.setStatus(Status.valueOf(400));
                res.send("error");
            }
        });

        app.post("/schematicfile", (req, res) -> {
            InputStream data = req.getBody();
            try {
                Scanner reader = new Scanner(data);
                String request = reader.hasNext() ? reader.next() : "";
                reader.close();
                File schem = new File("iocontent/" + request + ".msch");
                if (!schem.exists()){
                    res.setStatus(Status.valueOf(400));
                    res.send("bad request");
                }
                System.out.println("File requested, sending.");
                res.sendAttachment(schem.toPath());
            } catch (Exception e) {
                e.printStackTrace();
                res.setStatus(Status.valueOf(400));
                res.send("error");
            }
        });

        app.bind("127.0.0.1");
        app.listen(port);
    }
}

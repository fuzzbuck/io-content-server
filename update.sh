gradle dist || exit;
cp build/libs/content-server.jar ~/Games/Mindustry/Server/content-server/content-server.jar && echo "Done.";
/**
 * @author Roman Rusanov
 * @since 11.06.2021
 * email roman9628@gmail.com
 */
public class Start {
    public static void main(String[] args) {
        String properties = args.length == 0 ? "app.properties" : args[0];
        Config config = new Config(properties);
        Parser parser = new Parser(config.getConfig().getProperty("siteUrl"),
                new SiteLoader(),
                new Downloader(),
                new UnZip(),
                new FolderProcessor());
        parser.execute();
    }
}
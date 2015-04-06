package be.noselus.pictures;

import be.noselus.model.AssemblyEnum;
import be.noselus.util.Service;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Singleton
public class PictureManager implements Service {

    private final DataSource dataSource;
    private Map<Integer, ImageInfo> mapping = null;
    private final Set<Integer> personWithoutPicture = new HashSet<>();
    private final Set<Integer> personWithPicture = new HashSet<>();

    @Inject
    public PictureManager(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void start() {
        try (Connection db = dataSource.getConnection();
             PreparedStatement stat = db.prepareStatement("SELECT id, assembly_id, belong_to_assembly FROM person;")) {

            stat.execute();

            mapping = new TreeMap<>();
            while (stat.getResultSet().next()) {
                int id = stat.getResultSet().getInt("id");
                int idInAssembly = stat.getResultSet().getInt("assembly_id");
                int assembly = stat.getResultSet().getInt("belong_to_assembly");
                mapping.put(id, new ImageInfo(idInAssembly, assembly));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
    }

    public Set<ImageInfo> getMissingPictures(){
        Set<ImageInfo> result = new HashSet<>();
        if (mapping == null){
            return result;
        }
        for (Integer personId : personWithoutPicture) {
            result.add(mapping.get(personId));
        }
        return result;
    }

    public InputStream get(int id) {
        final ImageInfo imageInfo = mapping.get(id);
        if (imageInfo == null){
            return null;
        }
        if (personWithoutPicture.contains(id)) {
            return null;
        }
        if (imageInfo.hasImage()) {
            return PictureManager.class.getResourceAsStream(imageInfo.getImagePath());
        } else {
            return null;
        }
    }

    public void get(int id, int width, int height, OutputStream os) throws IOException {
        final ImageInfo imageInfo = mapping.get(id);
        if (imageInfo == null){
            return;
        }
        if (personWithoutPicture.contains(id)) {
            return;
        }

        if (imageInfo.hasImage()) {
            Thumbnails.of(PictureManager.class.getResourceAsStream(imageInfo.getImagePath()))
                    .size(width, height)
                    .crop(Positions.CENTER)
                    .imageType(BufferedImage.SCALE_FAST)
                    .outputFormat("jpg")
                    .toOutputStream(os);
        }
    }

    public boolean hasPicture(int personId) {
        if (personWithPicture.contains(personId)){
            return true;
        }
        if (personWithoutPicture.contains(personId)){
            return false;
        }
        try {
            final InputStream inputStream = get(personId);
            if (inputStream == null){
                personWithoutPicture.add(personId);
                return false;
            }
            personWithPicture.add(personId);
            return true;
        } catch (Exception e) {
            personWithoutPicture.add(personId);
            return false;
        }
    }

    private class ImageInfo {
        private final int personIdInAssembly;
        private final int assemblyId;

        private String path;
        private String ext;

        public ImageInfo(final int personIdInAssembly, final int assembly) {
            this.personIdInAssembly = personIdInAssembly;
            this.assemblyId = assembly;
            init();
        }

        public String getImagePath(){
            return path + personIdInAssembly + ext;
        }

        public boolean hasImage(){
            return path != null && ext != null;
        }

        private ImageInfo init() {
            if (assemblyId == AssemblyEnum.WAL.getId()) {
                path = "/pictures/parlement/";
                ext = ".jpg";
            } else if (assemblyId == AssemblyEnum.GVT_WAL.getId()) {
                path = "/pictures/minister/";
                ext = ".jpg";
            } else if (assemblyId == AssemblyEnum.FED.getId()) {
                path = "/pictures/chamber/";
                ext = ".gif";
            }
            return this;
        }
    }
}

package be.noselus.service;

import be.noselus.pictures.PictureManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import static be.noselus.service.RoutesHelper.getJson;

@Singleton
public class SupportRoutes implements Routes {

    private final PictureManager pictureManager;
    private final RoutesHelper helper;

    @Inject
    public SupportRoutes(final PictureManager pictureManager, final RoutesHelper helper) {
        this.pictureManager = pictureManager;
        this.helper = helper;
    }

    @Override
    public void setup() {
        getJson("/support/missing-pictures",
                (request, response) -> helper.resultAs("missingPictures", pictureManager.getMissingPictures()));

    }
}

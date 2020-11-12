package org.netcracker.project.model.enums;

public enum Theme {
    DIGITAL,
    GAME,
    WEB,
    PHOTO,
    BIOLOGY,
    ECONOMIC,
    ANALYTIC,
    URBANISM,
    VR,
    AR,
    DESIGN,
    MOBILE,
    PROGRAMMING,
    BIOINFORMATICS,
    SIMULATIONS,
    MODELING,
    METALWORKING,
    VISUAL,
    AI,
    IMAGE_RECOGNITION,
    HACK,
    PENTESTING,
    SECURITY;


    @Override
    public String toString() {
        String readable = name().replaceAll("_", " ");
        return name().length() > 3 ? readable.toLowerCase(): readable;
    }
}

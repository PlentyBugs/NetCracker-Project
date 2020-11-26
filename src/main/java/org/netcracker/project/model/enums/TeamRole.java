package org.netcracker.project.model.enums;

public enum  TeamRole {
    PROGRAMMER,
    DESIGNER,
    ANALYST,
    FRONTEND,
    BACKEND,
    SEO,
    PR,
    ENGINEER,
    ARTIST,
    BIOTECHNOLOGIST,
    BIOLOGIST,
    INTERIOR_DESIGNER,
    GAME_DESIGNER,
    ARCHITECT,
    SOUND_DESIGNER,
    MUSICIAN,
    SAFETY_ENGINEER,
    TESTER;


    @Override
    public String toString() {
        String readable = name().replaceAll("_", " ");
        return name().length() > 3 ? readable.toLowerCase(): readable;
    }
}

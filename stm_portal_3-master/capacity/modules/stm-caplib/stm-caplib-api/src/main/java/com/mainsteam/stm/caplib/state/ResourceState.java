package com.mainsteam.stm.caplib.state;

/**
 * Resource state collections.
 *
 * @author lich
 * @version 4.2.0
 * @since 4.2.0
 */
public class ResourceState {
    public final Availability availability;
    public final Collectibility collectibility;

    public ResourceState(Availability availability, Collectibility collectibility) {
        this.availability = availability;
        this.collectibility = collectibility;
    }
}

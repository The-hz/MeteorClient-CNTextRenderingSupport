package com.cjkfix.addon;

import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import org.slf4j.Logger;

public class CJKFixLoader extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        LOG.info("Initializing Meteor Addon Template");
    }

    @Override
    public void onRegisterCategories() {
    }

    @Override
    public String getPackage() {
        return "com.cjkfix.addon";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("The-hz", "meteor-client-cn-text-rendering-support");
    }
}

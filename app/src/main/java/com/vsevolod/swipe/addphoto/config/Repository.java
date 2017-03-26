package com.vsevolod.swipe.addphoto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vsevolod on 23.03.17.
 */

public class Repository {
    public List<String> paths;

    public Repository(List<String> paths) {
        this.paths = paths;
        inititalize();
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public String getPath(int position) {
        return this.paths.get(position);
    }

    public void setPath(String path) {
        this.paths.add(path);
    }

    public void setPath(String path, int position) {
        this.paths.add(position, path);
    }

    public void inititalize() {
        this.paths = new ArrayList<>();
        String path = "10";

        for (int i = 0; i < 100; i++) {
            if (i < 10) {
                this.paths.add(String.format("%s0%d", path, i));
            } else if (i > 9) {
                this.paths.add(String.format("%s%d", path, i));
            } else if (i > 20) {
                path = "20";
                this.paths.add(String.format("%s%d", path, i));
            } else if (i > 40) {
                path = "30";
                this.paths.add(String.format("%s%d", path, i));
            } else if (i > 60) {
                path = "40";
                this.paths.add(String.format("%s%d", path, i));
            } else if (i > 80) {
                path = "50";
                this.paths.add(String.format("%s%d", path, i));
            }
        }
    }
}

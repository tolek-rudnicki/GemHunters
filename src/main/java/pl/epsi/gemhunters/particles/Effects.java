package pl.epsi.gemhunters.particles;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import pl.epsi.gemhunters.Main;
import pl.epsi.gemhunters.libs.DrawLib;

public class Effects {

    private int taskID;
    private BukkitScheduler scheduler = Bukkit.getScheduler();
    private Plugin plugin = Main.getPlugin(Main.class);

    public Effects() {  }

    public int rubyShield(Entity p, Particle particle, int radius, int particles) {
        taskID = scheduler.scheduleSyncRepeatingTask(plugin, () -> {
            ParticleData data = new ParticleData(p.getUniqueId());

            if (!data.hasID()) { data.setID(taskID); return; }

            for (int i = 0; i < particles; i++) {
                double phi = Math.random() * Math.PI;
                double theta = Math.random() * 2 * Math.PI;

                double x = radius * Math.sin(phi) * Math.cos(theta);
                double y = radius * Math.cos(phi) + 1.5;
                double z = radius * Math.sin(phi) * Math.sin(theta);

                Location particleLocation = p.getLocation().add(x, y, z);
                p.getWorld().spawnParticle(particle, particleLocation, 0);
            }
        }, 0, 1);
        return taskID;
    }

    public int amethystAbility1(Entity p, Particle particle) {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), new Runnable() {

            double var = 0;
            Location loc, first, second;
            ParticleData data = new ParticleData(p.getUniqueId());

            @Override
            public void run() {
                if(!data.hasID()) { data.setID(taskID); return; }
                var += Math.PI / 16;
                loc = p.getLocation();

                first = loc.clone().add(Math.cos(var), Math.sin(var) + 1, Math.sin(var));
                second = loc.clone().add(Math.cos(var + Math.PI), Math.sin(var) + 1, Math.sin(var + Math.PI));

                p.getWorld().spawnParticle(particle, first, 0);
                p.getWorld().spawnParticle(particle, second, 0);
            }

        }, 0, 1);
        return taskID;
    }

    public int amethystAbility3(Entity p, Particle particle) {
        taskID = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            double radius = 2;
            double radiusStep = 0.005;
            double angle = 0;
            boolean isContracting = true;
            @Override
            public void run() {
                Location location = p.getLocation();

                if (angle < 2 * Math.PI) {
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);

                    Location particleLocation = location.clone().add(x, 0.85, z);

                    p.getWorld().spawnParticle(particle, particleLocation, 0);

                    angle += 0.1; // Adjust the angle increment for slower rendering
                    if (isContracting) {
                        radius -=radiusStep;
                        if (radius <= 0.5) {
                            isContracting = false;
                        }
                    } else {
                        radius += radiusStep;
                        if (radius >= 2.0) {
                            isContracting = true;
                        }
                    }
                } else {
                    // Adjust the radius for contracting and expanding movements
                    if (isContracting) {
                        radius -= radiusStep;
                        if (radius <= 0.5) {
                            isContracting = false;
                        }
                    } else {
                        radius += radiusStep;
                        if (radius >= 2.0) {
                            isContracting = true;
                        }
                    }
                    angle = 0.0; // Reset angle for the next iteration
                }
            }

    }, 0, 1);
        return taskID;
    }

    public int startCircle(Entity player, Particle particles, int radius) {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), new Runnable() {
            double var = 0;
            Location loc, first, second;
            ParticleData particle = new ParticleData(player.getUniqueId());

            @Override
            public void run() {
                if(!particle.hasID()) { particle.setID(taskID); return; }
                var += Math.PI / 16;
                loc = player.getLocation();
                double u = loc.getY();

                int a = 0;
                while (a < 360) {
                    first = loc.clone().add(Math.cos(a) * radius, u, Math.sin(a) * radius);
                    first.setY(u);
                    //player.sendMessage(a + "!");
                    //player.sendMessage(first + "!");
                    //player.getWorld().getBlockAt(first).setType(Material.BLACK_CONCRETE);
                    player.getWorld().spawnParticle(Particle.FLAME, first, 0);
                    a += 5;
                }
            }
        }, 0, 1);
        return taskID;
    }

    public int startCircleCage(Location loc, Particle particles, int radius, Entity e) {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), new Runnable() {
            double var = 0;
            Location first, second;
            ParticleData particle = new ParticleData(e.getUniqueId());

            @Override
            public void run() {
                if(!particle.hasID()) { particle.setID(taskID); return; }
                var += Math.PI / 16;
                double u = loc.getY();

                int a = 0;
                while (a < 360) {
                    first = loc.clone().add(Math.cos(a) * radius, u, Math.sin(a) * radius);
                    first.setY(u + e.getHeight() + 1.2);
                    second = loc.clone().add(Math.cos(a) * radius, u - 0.5, Math.sin(a) * radius);
                    second.setY(u - 0.5);
                    e.getWorld().spawnParticle(particles, first, 0);
                    e.getWorld().spawnParticle(particles, second, 0);
                    a += 5;
                }
            }
        }, 0, 1);
        return taskID;
    }

    public void stopTask(int task) {
        Bukkit.getScheduler().cancelTask(task);
    }

}

package me.lucko.spark.minestom;

import java.nio.file.Path;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import me.lucko.spark.common.SparkPlatform;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main entry point for the Minestom port of Spark
 */
public final class SparkMinestom {

    private final @NotNull MinestomSparkPlugin plugin;
    private final @NotNull SparkPlatform platform;

    private SparkMinestom(@NotNull MinestomSparkPlugin plugin, @NotNull SparkPlatform platform) {
        this.plugin = plugin;
        this.platform = platform;
    }

    /**
     * Creates a new SparkMinestom builder
     *
     * @param dataDirectory the data directory to store all Spark data
     * @return a new builder
     */
    public static @NotNull Builder builder(@NotNull Path dataDirectory) {
        return new BuilderImpl(dataDirectory);
    }

    /**
     * Shuts down the Spark instance
     */
    public void shutdown() {
        this.plugin.terminate();
    }

    /**
     * Gets the Spark native platform instance
     *
     * @return the platform instance
     */
    public @NotNull SparkPlatform platform() {
        return platform;
    }

    /**
     * A builder for creating a new SparkMinestom instance
     */
    public sealed interface Builder permits BuilderImpl {

        /**
         * Sets whether the Spark commands should be registered
         *
         * @param enabled if commands should be enabled
         * @return this builder
         */
        @Contract("_ -> this")
        @NotNull Builder commands(boolean enabled);

        /**
         * Sets the logger to use
         *
         * @param logger the logger
         * @return this builder
         */
        @Contract("_ -> this")
        @NotNull Builder logger(@NotNull Logger logger);

        /**
         * Sets the permission handler to use
         *
         * @param permissionHandler the permission handler
         * @return this builder
         */
        @Contract("_ -> this")
        @NotNull Builder permissionHandler(@NotNull BiPredicate<CommandSender, String> permissionHandler);

        /**
         * Enables Spark
         *
         * @return the newly created SparkMinestom instance
         */
        @NotNull SparkMinestom enable();

    }

    private static final class BuilderImpl implements Builder {

        private final @NotNull Path dataDirectory;

        private boolean commands = false;
        private @NotNull BiPredicate<CommandSender, String> permissionHandler = (sender, permission) -> sender instanceof ConsoleSender;
        private @NotNull Logger logger = LoggerFactory.getLogger(SparkMinestom.class);

        private BuilderImpl(@NotNull Path dataDirectory) {
            this.dataDirectory = dataDirectory;
        }

        @Override
        public @NotNull Builder commands(boolean enabled) {
            this.commands = enabled;
            return this;
        }

        @Override
        public @NotNull Builder logger(@NotNull Logger logger) {
            this.logger = logger;
            return this;
        }

        @Override
        public @NotNull Builder permissionHandler(@NotNull BiPredicate<CommandSender, String> permissionHandler) {
            this.permissionHandler = permissionHandler;
            return this;
        }

        @Override
        public @NotNull SparkMinestom enable() {
            MinestomSparkPlugin plugin = new MinestomSparkPlugin(this.dataDirectory, this.logger, this.commands, this.permissionHandler);
            return new SparkMinestom(plugin, plugin.initialize());
        }

    }


}

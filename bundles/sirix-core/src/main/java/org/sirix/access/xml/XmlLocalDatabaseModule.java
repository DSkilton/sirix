package org.sirix.access.xml;

import dagger.Module;
import dagger.Provides;
import org.sirix.access.DatabaseConfiguration;
import org.sirix.access.LocalDatabase;
import org.sirix.access.LocalDatabaseModule;
import org.sirix.access.PathBasedPool;
import org.sirix.access.ResourceManagerFactory;
import org.sirix.access.ResourceStore;
import org.sirix.access.ResourceStoreImpl;
import org.sirix.access.SubComponentResourceManagerFactory;
import org.sirix.access.WriteLocksRegistry;
import org.sirix.api.Database;
import org.sirix.api.ResourceManager;
import org.sirix.api.TransactionManager;
import org.sirix.api.xml.XmlResourceManager;
import org.sirix.dagger.DatabaseScope;

import javax.inject.Provider;

/**
 * The module for {@link XmlLocalDatabaseComponent}.
 *
 * @author Joao Sousa
 */
@Module(includes = LocalDatabaseModule.class)
public interface XmlLocalDatabaseModule {

    @DatabaseScope
    @Provides
    static ResourceManagerFactory<XmlResourceManager> resourceManagerFactory(
            final Provider<XmlResourceManagerComponent.Builder> subComponentBuilder) {

        return new SubComponentResourceManagerFactory<>(subComponentBuilder);
    }

    @DatabaseScope
    @Provides
    static Database<XmlResourceManager> xmlDatabase(final TransactionManager transactionManager,
            final DatabaseConfiguration dbConfig,
            final PathBasedPool<Database<?>> sessions,
            final ResourceStore<XmlResourceManager> resourceStore,
            final WriteLocksRegistry writeLocks,
            final PathBasedPool<ResourceManager<?, ?>> resourceManagers) {

        return new LocalDatabase<>(
                transactionManager,
                dbConfig,
                sessions,
                resourceStore,
                writeLocks,
                resourceManagers
        );
    }

    @DatabaseScope
    @Provides
    static ResourceStore<XmlResourceManager> xmlResourceManager(
            final PathBasedPool<ResourceManager<?, ?>> allResourceManagers,
            final ResourceManagerFactory<XmlResourceManager> resourceManagerFactory) {

        return new ResourceStoreImpl<>(allResourceManagers, resourceManagerFactory);
    }
}

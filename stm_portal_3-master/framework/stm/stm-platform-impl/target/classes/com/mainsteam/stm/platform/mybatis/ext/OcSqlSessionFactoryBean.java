//package com.mainsteam.stm.platform.mybatis.ext;
//
//import static org.springframework.util.ObjectUtils.isEmpty;
//import static org.springframework.util.StringUtils.hasLength;
//import static org.springframework.util.StringUtils.tokenizeToStringArray;
//
//import java.io.IOException;
//import java.sql.SQLException;
//import java.util.Properties;
//
//import javax.sql.DataSource;
//
//import org.apache.ibatis.builder.xml.XMLConfigBuilder;
//import org.apache.ibatis.builder.xml.XMLMapperBuilder;
//import org.apache.ibatis.executor.ErrorContext;
//import org.apache.ibatis.logging.Log;
//import org.apache.ibatis.logging.LogFactory;
//import org.apache.ibatis.mapping.DatabaseIdProvider;
//import org.apache.ibatis.mapping.Environment;
//import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
//import org.apache.ibatis.plugin.Interceptor;
//import org.apache.ibatis.reflection.factory.ObjectFactory;
//import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
//import org.apache.ibatis.session.Configuration;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.apache.ibatis.session.SqlSessionFactoryBuilder;
//import org.apache.ibatis.transaction.TransactionFactory;
//import org.apache.ibatis.type.TypeHandler;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.core.NestedIOException;
//import org.springframework.core.io.Resource;
//
///**
// * <li>文件名称: SqlSessionFactoryBean.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>
// * 版权所有: 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明:
// * ...</li>
// * 
// * @version ms.stm
// * @since 2019年6月17日
// * @author ziwenwen
// */
//public class OcSqlSessionFactoryBean extends SqlSessionFactoryBean {
//	private final Log logger = LogFactory.getLog(getClass());
//
//	private Resource configLocation;
//
//	private Resource[] mapperLocations;
//
//	private DataSource dataSource;
//
//	private TransactionFactory transactionFactory;
//
//	private Properties configurationProperties;
//
//	private SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
//
//	private String environment = SqlSessionFactoryBean.class.getSimpleName(); // EnvironmentAware
//																				// requires
//																				// spring
//																				// 3.1
//
//	private Interceptor[] plugins;
//
//	private TypeHandler<?>[] typeHandlers;
//
//	private String typeHandlersPackage;
//
//	private Class<?>[] typeAliases;
//
//	private String typeAliasesPackage;
//
//	private Class<?> typeAliasesSuperType;
//
//	private DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
//
//	private ObjectFactory objectFactory;
//
//	private ObjectWrapperFactory objectWrapperFactory;
//
//	@Override
//	protected SqlSessionFactory buildSqlSessionFactory() throws IOException {
//
//		Configuration configuration;
//
//		XMLConfigBuilder xmlConfigBuilder = null;
//		if (this.configLocation != null) {
//			xmlConfigBuilder = new XMLConfigBuilder(
//					this.configLocation.getInputStream(), null,
//					this.configurationProperties);
//			configuration = xmlConfigBuilder.getConfiguration();
//		} else {
//			if (this.logger.isDebugEnabled()) {
//				this.logger
//						.debug("Property 'configLocation' not specified, using default MyBatis Configuration");
//			}
//			configuration = new Configuration();
//			configuration.setVariables(this.configurationProperties);
//		}
//
//		if (this.objectFactory != null) {
//			configuration.setObjectFactory(this.objectFactory);
//		}
//
//		if (this.objectWrapperFactory != null) {
//			configuration.setObjectWrapperFactory(this.objectWrapperFactory);
//		}
//
//		if (hasLength(this.typeAliasesPackage)) {
//			String[] typeAliasPackageArray = tokenizeToStringArray(
//					this.typeAliasesPackage,
//					ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
//			for (String packageToScan : typeAliasPackageArray) {
//				configuration.getTypeAliasRegistry().registerAliases(
//						packageToScan,
//						typeAliasesSuperType == null ? Object.class
//								: typeAliasesSuperType);
//				if (this.logger.isDebugEnabled()) {
//					this.logger.debug("Scanned package: '" + packageToScan
//							+ "' for aliases");
//				}
//			}
//		}
//
//		if (!isEmpty(this.typeAliases)) {
//			for (Class<?> typeAlias : this.typeAliases) {
//				configuration.getTypeAliasRegistry().registerAlias(typeAlias);
//				if (this.logger.isDebugEnabled()) {
//					this.logger.debug("Registered type alias: '" + typeAlias
//							+ "'");
//				}
//			}
//		}
//
//		if (!isEmpty(this.plugins)) {
//			for (Interceptor plugin : this.plugins) {
//				configuration.addInterceptor(plugin);
//				if (this.logger.isDebugEnabled()) {
//					this.logger.debug("Registered plugin: '" + plugin + "'");
//				}
//			}
//		}
//
//		if (hasLength(this.typeHandlersPackage)) {
//			String[] typeHandlersPackageArray = tokenizeToStringArray(
//					this.typeHandlersPackage,
//					ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
//			for (String packageToScan : typeHandlersPackageArray) {
//				configuration.getTypeHandlerRegistry().register(packageToScan);
//				if (this.logger.isDebugEnabled()) {
//					this.logger.debug("Scanned package: '" + packageToScan
//							+ "' for type handlers");
//				}
//			}
//		}
//
//		if (!isEmpty(this.typeHandlers)) {
//			for (TypeHandler<?> typeHandler : this.typeHandlers) {
//				configuration.getTypeHandlerRegistry().register(typeHandler);
//				if (this.logger.isDebugEnabled()) {
//					this.logger.debug("Registered type handler: '"
//							+ typeHandler + "'");
//				}
//			}
//		}
//
//		if (xmlConfigBuilder != null) {
//			try {
//				xmlConfigBuilder.parse();
//
//				if (this.logger.isDebugEnabled()) {
//					this.logger.debug("Parsed configuration file: '"
//							+ this.configLocation + "'");
//				}
//			} catch (Exception ex) {
//				throw new NestedIOException("Failed to parse config resource: "
//						+ this.configLocation, ex);
//			} finally {
//				ErrorContext.instance().reset();
//			}
//		}
//
//		if (this.transactionFactory == null) {
//			this.transactionFactory = new SpringManagedTransactionFactory();
//		}
//
//		Environment environment = new Environment(this.environment,
//				this.transactionFactory, this.dataSource);
//		configuration.setEnvironment(environment);
//
//		if (this.databaseIdProvider != null) {
//			try {
//				configuration.setDatabaseId(this.databaseIdProvider
//						.getDatabaseId(this.dataSource));
//			} catch (SQLException e) {
//				throw new NestedIOException("Failed getting a databaseId", e);
//			}
//		}
//
//		if (!isEmpty(this.mapperLocations)) {
//			for (Resource mapperLocation : this.mapperLocations) {
//				if (mapperLocation == null) {
//					continue;
//				}
//
//				try {
//					mapperLocation.getURI();dd
//					XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(
//							mapperLocation.getInputStream(), configuration,
//							mapperLocation.toString(),
//							configuration.getSqlFragments());
//					xmlMapperBuilder.parse();
//				} catch (Exception e) {
//					throw new NestedIOException(
//							"Failed to parse mapping resource: '"
//									+ mapperLocation + "'", e);
//				} finally {
//					ErrorContext.instance().reset();
//				}
//
//				if (this.logger.isDebugEnabled()) {
//					this.logger.debug("Parsed mapper file: '" + mapperLocation
//							+ "'");
//				}
//			}
//		} else {
//			if (this.logger.isDebugEnabled()) {
//				this.logger
//						.debug("Property 'mapperLocations' was not specified or no matching resources found");
//			}
//		}
//
//		return this.sqlSessionFactoryBuilder.build(configuration);
//	}
//}

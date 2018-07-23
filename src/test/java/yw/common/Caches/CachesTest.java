package yw.common.Caches;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class CachesTest {

	@Test
	public void test() {


		LoadingCache<String, Object> graphs = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.SECONDS)
				.build(new CacheLoader<String, Object>() {
					@Override
					public Object load(String key) throws Exception {
						// 如果找缓存中没有找到对应数据，则采用的加载逻辑
						return "100";
					}
				});

		graphs.put("1", 1);
		graphs.put("2", 2);
		graphs.put("3", 3);


		try {

			System.out.println(graphs.get("1"));
			System.out.println(graphs.get("2"));
			System.out.println(graphs.get("3"));

			Thread.sleep(2 * 1000);

			System.out.println(graphs.get("1"));
			System.out.println(graphs.get("2"));
			System.out.println(graphs.get("3"));

			Thread.sleep(6 * 1000);

			System.out.println(graphs.get("1"));
			System.out.println(graphs.get("2"));
			System.out.println(graphs.get("3"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

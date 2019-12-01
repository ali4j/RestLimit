package net.ali4j.restlimit.restlimit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class RestlimitApplicationTests {

	static class TestClassSuper{
		public TestClassSuper(){
			System.out.println("inside super class constructor");
		}
	}

	static class TestClassChild extends  TestClassSuper {

		{
			System.out.println("inside initializer block 1st");
		}

		{
			System.out.println("inside initializer block 2nd");
		}


		public TestClassChild(){
			super();
			System.out.println("inside child class constructor");
		}

	}

	public static void main(String[] args) {
		TestClassChild ss = new TestClassChild();
		System.out.println(ss);
	}

//	@Test
//	public void contextLoads() {
//	}

}

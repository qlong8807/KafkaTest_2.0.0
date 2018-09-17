package com.zz.test.java8.lambda;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Maps;


public class PersonTest {
	public static void main(String[] args) {
		List<Person> persons = generateData();
		Objects.requireNonNull(persons);//如果为null，则抛出异常，中断执行
		Map<String, Integer> mapData = generateMapData();
		System.out.println("薪资超过6000的人有：");
		persons.stream().filter(p -> p.getSalary() >= 6000).forEach(p -> System.out.printf("%s,", p.getName()));
		System.out.println();
		System.out.println("遍历map 一：");
		mapData.forEach((k,v) -> System.out.print(k+"---"+v + " , "));
		System.out.println();
		System.out.println("遍历map 二：");
		mapData.entrySet().stream().forEach(entry -> System.out.print(entry.getKey()+"---"+entry.getValue() + " , "));
		System.out.println();
		System.out.println("java8 分组操作");
		Map<String, List<Person>> collect = persons.stream().collect(Collectors.groupingBy(p -> p.getCity()));
		System.err.println(collect);
        System.out.println("---->>>>List转化为Map>>>----");
        Map<String, String> collect2 = persons.stream().collect(Collectors.toMap(p->p.getName(), Person::getCity));
        System.err.println(collect2);
        //根据equals查询指定对象
        List<Person> collect3 = persons.stream().filter(p -> "Steve".equals(p.getName())).collect(Collectors.toList());
		collect3.forEach(c -> System.out.println(c.getName()));
		
		//anyMatcher
		boolean anyMatch = persons.stream().anyMatch(c -> c.getCity().equals("New York"));
		System.err.println("anyMatch(包含)NewYork:"+anyMatch);
		boolean allMatch = persons.stream().allMatch(a -> "London".equals(a.getCity()));
		System.err.println("allMatch(全部包含)NewYork:"+allMatch);
		System.err.print("薪资最高的是:");
		Optional<Person> max = persons.stream().max((a,b) -> Integer.compare(a.getSalary(),b.getSalary()));
		System.out.println(max.get().getName());
		System.err.print("薪资最高的前二人是:");
		persons.stream().sorted((a,b)->Integer.compare(b.getSalary(), a.getSalary())).limit(2).forEach(p -> System.out.print(p.getName()+" "));
		System.err.println("\n-----------------------------------");
		System.err.print("在New York的人个数:");
		long count = persons.stream().filter(p -> "New York".equals(p.getCity())).count();
		System.out.println(count);
		System.out.print("查询各个城市的人数:");
		persons.stream().collect(Collectors.groupingBy(Person::getCity)).forEach((k,v) -> System.out.print(k+"---"+v.size()+" , "));
		System.err.println("\n-----------------------------------");
		System.out.print("查询各个城市的人数2:");
		persons.stream().collect(Collectors.groupingBy(Person::getCity,Collectors.summingInt(p -> 1))).forEach((k,v) -> System.out.print(k+"---"+v+" , "));
		System.err.println("\n-----------------------------------");
		System.out.print("查询各个城市的人名:");
		persons.stream().collect(Collectors.groupingBy(Person::getCity,Collectors.mapping(Person::getName, Collectors.toList()))).forEach((k,v) -> System.out.print(k+"---"+v+" , "));
		System.err.println("\n-----------------------------------");
		
		System.out.println("List转Set：");
		Set<String> collect4 = persons.stream().map(p -> p.getCity()).distinct().collect(Collectors.toSet());
		System.err.println(collect4);
		
		System.out.println("查找New York的员工:");
		List<Person> nPersons = persons.stream().filter(p -> "New York".equals(p.getCity())).collect(Collectors.toList());
		nPersons.forEach(p -> System.out.print(p.getName()+"  "));
		System.err.println("\n-----------------------------------");
//		降级排序
		System.out.print("薪资降级排序(注意compare顺序):");
		persons.stream().sorted((a,b) -> Integer.compare(b.getSalary(), a.getSalary())).forEach(p -> System.out.print(p.getName() +"("+p.getSalary()+")"+" > "));
		System.err.println("\n-----------------------------------");
		System.out.print("姓名升级排序(注意compare顺序):");
		persons.stream().sorted((a,b) -> a.getName().compareTo(b.getName())).collect(Collectors.toList()).forEach(p -> System.out.print(p.getName() +" > "));
		System.err.println("\n-----------------------------------");
		System.out.print("平均工资:");
		OptionalDouble average = persons.stream().mapToInt(Person::getSalary).average();
		System.out.println(average.getAsDouble());
		System.out.print("Optional用法:");
		Optional<String> of = Optional.of("hello");
		Optional<Double> of2 = Optional.of(123.456);
		System.out.println(of.get() + of2.get());
		
		List<Integer> collect5 = mapData.entrySet().stream().map(m -> m.getValue()).collect(Collectors.toList());
		System.out.println(collect5);
		System.err.println("测试map方法----------------------");
		List<Integer> numbers = Arrays.asList(1, 2, 3, 4);
		List<String> collect6 = numbers.parallelStream().map(num -> "##"+num).collect(Collectors.toList());
		System.out.println(collect6.toString());
		
		System.err.println("测试reduce方法----------------------");
		//1.不提供初始值的reduce，返回值是Optional，表示可能为空，使用orElseGet可以返回一个null时的默认值
		//2.使用初始值的reduce，因为提供了初始值，所以返回值不再是Optional
		Optional accResult = Stream.of(1, 2, 3, 4)
		        .reduce((acc, item) -> {
		            System.out.println("acc : "  + acc);
		            acc += item;
		            System.out.println("item: " + item);
		            System.out.println("acc+ : "  + acc);
		            System.out.println("--------");
		            return acc;
		        });
		System.out.println(accResult.get());
		
		String s = "hello,world";
		List<String> collect7 = Stream.of(s).map(a -> {System.err.println(a);return a.split("");}).flatMap(str -> Arrays.stream(str)).distinct().collect(Collectors.toList());
		System.err.println(collect7.size());
	}
	
    public static List<Person> generateData() {
        return Arrays.asList(new Person("Matt", 5000, "New York"),
                new Person("Steve", 6000, "London"),
                new Person("Carrie", 10000, "New York"),
                new Person("Peter", 7000, "New York"),
                new Person("Alec", 6000, "London"),
                new Person("Sarah", 8000, "London"),
                new Person("Rebecca", 4000, "New York"),
                new Person("Pat", 20000, "New York"),
                new Person("Tammy", 9000, "New York"),
                new Person("Fred", 15000, "Tokyo"));
    }
 
    public static Map<String, Integer> generateMapData() {
        Map<String, Integer> items = Maps.newHashMap();
        items.put("A", 10);
        items.put("B", 20);
        items.put("C", 30);
        items.put("D", 40);
        items.put("E", 50);
        items.put("F", 60);
 
        return items;
    }
}

class Person{
	private String name;
	private Integer salary;
	private String city;
	
	public Person(String name, Integer salary, String city) {
		super();
		this.name = name;
		this.salary = salary;
		this.city = city;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getSalary() {
		return salary;
	}
	public void setSalary(Integer salary) {
		this.salary = salary;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
}
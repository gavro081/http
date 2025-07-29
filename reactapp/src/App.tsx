import { createBrowserRouter, Outlet, RouterProvider } from "react-router-dom";
import Footer from "./components/Footer.tsx";
import LandingPage from "./components/LandingPage.tsx";
import Nav from "./components/Nav.tsx";
import NotFound from "./components/NotFound.tsx";
import Test from "./components/Test.tsx";

const Layout = () => (
	<div className="flex flex-col min-h-screen">
		<Nav />
		<main className="flex-grow">
			<Outlet />
		</main>
		<Footer />
	</div>
);

const router = createBrowserRouter([
	{
		path: "/",
		element: <Layout />,
		children: [
			{
				path: "/",
				element: <LandingPage />,
			},
			{
				path: "/test",
				element: <Test />,
			},
			{
				path: "*",
				element: <NotFound />,
			},
		],
	},
]);

const App = () => {
	return (
		<>
			<RouterProvider router={router} />
		</>
	);
};

export default App;

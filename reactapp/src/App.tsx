import { createBrowserRouter, RouterProvider } from "react-router-dom";
import LandingPage from "./components/LandingPage.tsx";
import NotFound from "./components/NotFound.tsx";
import Test from "./components/Test.tsx";

const router = createBrowserRouter([
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
]);

const App = () => {
	return (
		<>
			<RouterProvider router={router} />
		</>
	);
};

export default App;
